import type { Express } from "express";
import { createServer, type Server } from "http";
import Stripe from "stripe";
import { storage } from "./storage";
import { db, pool } from "./db";
import { 
  insertEmployeeProfileSchema, 
  insertEmployerProfileSchema,
  insertAdvanceRequestSchema,
  advanceRequestStatusEnum
} from "../shared/schema";
import { z } from "zod";

// Initialize Stripe if secret key is available
let stripe: Stripe | undefined;
try {
  if (process.env.STRIPE_SECRET_KEY) {
    stripe = new Stripe(process.env.STRIPE_SECRET_KEY, {
      apiVersion: "2023-10-16" as any, // Type cast to fix version mismatch
    });
  } else {
    console.warn('Missing Stripe secret key: STRIPE_SECRET_KEY');
  }
} catch (error) {
  console.error('Error initializing Stripe:', error);
}

export function registerRoutes(app: Express): Server {
  // Middleware to check for stripe availability
  const checkStripeAvailability = (req: any, res: any, next: any) => {
    if (!stripe) {
      return res.status(503).json({ error: "Payment service unavailable" });
    }
    next();
  };

  // User profile routes
  
  // Get employee profile
  app.get("/api/user/employee-profile", async (req: any, res: any) => {
    try {
      // Use non-null assertion to avoid TypeScript error
      const profile = await storage.getEmployeeProfileByUserId(req.user!.id);
      
      if (!profile) {
        return res.status(404).json({ error: "Employee profile not found" });
      }
      
      return res.json({ profile });
    } catch (error) {
      console.error("Error fetching employee profile:", error);
      return res.status(500).json({ error: "Failed to fetch employee profile" });
    }
  });
  
  // Create or update employee profile
  app.post("/api/user/employee-profile", async (req: any, res: any) => {
    try {
      if (!req.user) {
        return res.status(401).json({ error: "Authentication required" });
      }

      const validation = insertEmployeeProfileSchema.safeParse(req.body);
      
      if (!validation.success) {
        return res.status(400).json({ error: validation.error.format() });
      }
      
      const existingProfile = await storage.getEmployeeProfileByUserId(req.user.id);
      
      if (existingProfile) {
        // Update existing profile
        const updatedProfile = await storage.updateEmployeeProfile(
          existingProfile.id,
          { ...validation.data, userId: req.user.id }
        );
        return res.json({ profile: updatedProfile });
      } else {
        // Create new profile
        const newProfile = await storage.createEmployeeProfile({
          ...validation.data,
          userId: req.user.id
        });
        return res.status(201).json({ profile: newProfile });
      }
    } catch (error) {
      console.error("Error saving employee profile:", error);
      return res.status(500).json({ error: "Failed to save employee profile" });
    }
  });
  
  // Get employer profile
  app.get("/api/user/employer-profile", async (req: any, res: any) => {
    try {
      if (!req.user) {
        return res.status(401).json({ error: "Authentication required" });
      }

      const profile = await storage.getEmployerProfileByUserId(req.user.id);
      
      if (!profile) {
        return res.status(404).json({ error: "Employer profile not found" });
      }
      
      return res.json({ profile });
    } catch (error) {
      console.error("Error fetching employer profile:", error);
      return res.status(500).json({ error: "Failed to fetch employer profile" });
    }
  });
  
  // Create or update employer profile
  app.post("/api/user/employer-profile", async (req: any, res: any) => {
    try {
      if (!req.user) {
        return res.status(401).json({ error: "Authentication required" });
      }
      
      const validation = insertEmployerProfileSchema.safeParse(req.body);
      
      if (!validation.success) {
        return res.status(400).json({ error: validation.error.format() });
      }
      
      const existingProfile = await storage.getEmployerProfileByUserId(req.user.id);
      
      if (existingProfile) {
        // Update existing profile
        const updatedProfile = await storage.updateEmployerProfile(
          existingProfile.id,
          { ...validation.data, userId: req.user.id }
        );
        return res.json({ profile: updatedProfile });
      } else {
        // Create new profile
        const newProfile = await storage.createEmployerProfile({
          ...validation.data,
          userId: req.user.id
        });
        return res.status(201).json({ profile: newProfile });
      }
    } catch (error) {
      console.error("Error saving employer profile:", error);
      return res.status(500).json({ error: "Failed to save employer profile" });
    }
  });
  
  // Advance request routes
  
  // Get all advance requests for an employee
  app.get("/api/advance-requests", async (req: any, res: any) => {
    try {
      if (!req.user) {
        return res.status(401).json({ error: "Authentication required" });
      }

      const employeeProfile = await storage.getEmployeeProfileByUserId(req.user.id);
      
      if (!employeeProfile) {
        return res.status(404).json({ error: "Employee profile not found" });
      }
      
      const requests = await storage.getAdvanceRequestsByEmployeeId(employeeProfile.id);
      return res.json({ requests });
    } catch (error) {
      console.error("Error fetching advance requests:", error);
      return res.status(500).json({ error: "Failed to fetch advance requests" });
    }
  });
  
  // Get a specific advance request
  app.get("/api/advance-requests/:id", async (req: any, res: any) => {
    try {
      if (!req.user) {
        return res.status(401).json({ error: "Authentication required" });
      }
      
      const requestId = parseInt(req.params.id);
      
      if (isNaN(requestId)) {
        return res.status(400).json({ error: "Invalid request ID" });
      }
      
      const request = await storage.getAdvanceRequest(requestId);
      
      if (!request) {
        return res.status(404).json({ error: "Advance request not found" });
      }
      
      // Make sure the user has permission to view this request
      const employeeProfile = await storage.getEmployeeProfileByUserId(req.user.id);
      
      if (employeeProfile && request.employeeId === employeeProfile.id) {
        return res.json({ request });
      } else {
        // For employers, check if they own the employee
        const employerProfile = await storage.getEmployerProfileByUserId(req.user.id);
        
        if (employerProfile) {
          const employee = await storage.getEmployeeProfile(request.employeeId);
          
          if (employee && employee.employerId === employerProfile.id) {
            return res.json({ request });
          }
        }
        
        return res.status(403).json({ error: "Not authorized to view this request" });
      }
    } catch (error) {
      console.error("Error fetching advance request:", error);
      return res.status(500).json({ error: "Failed to fetch advance request" });
    }
  });
  
  // Create a new advance request
  app.post("/api/advance-requests", async (req: any, res: any) => {
    try {
      if (!req.user) {
        return res.status(401).json({ error: "Authentication required" });
      }

      const employeeProfile = await storage.getEmployeeProfileByUserId(req.user.id);
      
      if (!employeeProfile) {
        return res.status(404).json({ error: "Employee profile not found" });
      }
      
      const requestData = {
        ...req.body,
        employeeId: employeeProfile.id,
      };
      
      const validation = insertAdvanceRequestSchema.safeParse(requestData);
      
      if (!validation.success) {
        return res.status(400).json({ error: validation.error.format() });
      }
      
      // Validate request amount against salary and max advance percentage
      const maxAdvancePercentage = employeeProfile.maxAdvancePercentage || 30; // Default to 30% if not set
      const maxAdvanceAmount = (employeeProfile.salary * maxAdvancePercentage) / 100;
      
      if (requestData.requestedAmount > maxAdvanceAmount) {
        return res.status(400).json({ 
          error: `Requested amount exceeds your maximum advance limit of ${maxAdvanceAmount / 100} (${maxAdvancePercentage}% of your salary)` 
        });
      }
      
      const newRequest = await storage.createAdvanceRequest(validation.data);
      
      // Create a notification for the employee
      await storage.createNotification({
        userId: req.user.id,
        title: "Advance Request Submitted",
        message: `Your advance request for $${newRequest.requestedAmount / 100} has been submitted and is pending approval.`,
        type: "ADVANCE_REQUEST",
        relatedEntityId: newRequest.id,
        relatedEntityType: "ADVANCE_REQUEST",
      });
      
      // If there's an employer, create a notification for them too
      if (employeeProfile.employerId) {
        const employer = await storage.getEmployerProfile(employeeProfile.employerId);
        
        if (employer) {
          await storage.createNotification({
            userId: employer.userId,
            title: "New Advance Request",
            message: `Employee ${req.user.firstName} ${req.user.lastName} has requested a salary advance of $${newRequest.requestedAmount / 100}.`,
            type: "ADVANCE_REQUEST",
            relatedEntityId: newRequest.id,
            relatedEntityType: "ADVANCE_REQUEST",
          });
        }
      }
      
      return res.status(201).json({ request: newRequest });
    } catch (error) {
      console.error("Error creating advance request:", error);
      return res.status(500).json({ error: "Failed to create advance request" });
    }
  });
  
  // Approve or reject an advance request (employer only)
  app.patch("/api/advance-requests/:id/status", async (req: any, res: any) => {
    try {
      if (!req.user) {
        return res.status(401).json({ error: "Authentication required" });
      }
      
      const requestId = parseInt(req.params.id);
      
      if (isNaN(requestId)) {
        return res.status(400).json({ error: "Invalid request ID" });
      }
      
      const { status } = req.body;
      
      // Validate status
      const statusSchema = z.enum(["APPROVED", "REJECTED"]);
      const statusValidation = statusSchema.safeParse(status);
      
      if (!statusValidation.success) {
        return res.status(400).json({ error: "Invalid status. Must be APPROVED or REJECTED" });
      }
      
      // Check if user is an employer
      if (!req.user || req.user.role !== "EMPLOYER") {
        return res.status(403).json({ error: "Only employers can approve or reject advance requests" });
      }
      
      // Get the advance request
      const request = await storage.getAdvanceRequest(requestId);
      
      if (!request) {
        return res.status(404).json({ error: "Advance request not found" });
      }
      
      // Check if request is already processed
      if (request.status !== "PENDING") {
        return res.status(400).json({ error: `Request is already ${request.status.toLowerCase()}` });
      }
      
      // Check if employer has permission to approve this request
      const employerProfile = await storage.getEmployerProfileByUserId(req.user.id);
      
      if (!employerProfile) {
        return res.status(404).json({ error: "Employer profile not found" });
      }
      
      const employee = await storage.getEmployeeProfile(request.employeeId);
      
      if (!employee || employee.employerId !== employerProfile.id) {
        return res.status(403).json({ error: "Not authorized to update this request" });
      }
      
      // Update the request status
      const updatedRequest = await storage.updateAdvanceRequest(requestId, {
        status: statusValidation.data,
        approvedBy: req.user.id,
        approvedDate: new Date(),
      });
      
      // Create a notification for the employee
      const employeeUser = await storage.getUser(employee.userId);
      
      if (employeeUser) {
        await storage.createNotification({
          userId: employeeUser.id,
          title: `Advance Request ${status === "APPROVED" ? "Approved" : "Rejected"}`,
          message: `Your advance request for $${request.requestedAmount / 100} has been ${status === "APPROVED" ? "approved" : "rejected"}.`,
          type: "APPROVAL",
          relatedEntityId: request.id,
          relatedEntityType: "ADVANCE_REQUEST",
        });
      }
      
      return res.json({ request: updatedRequest });
    } catch (error) {
      console.error("Error updating advance request:", error);
      return res.status(500).json({ error: "Failed to update advance request" });
    }
  });
  
  // Payment endpoints (Stripe)
  
  // Create a payment intent for advance disbursement
  app.post("/api/payments/create-disbursement", checkStripeAvailability, async (req: any, res: any) => {
    try {
      if (!req.user) {
        return res.status(401).json({ error: "Authentication required" });
      }
      
      const { advanceRequestId } = req.body;
      
      if (!advanceRequestId) {
        return res.status(400).json({ error: "Missing advance request ID" });
      }
      
      const request = await storage.getAdvanceRequest(advanceRequestId);
      
      if (!request) {
        return res.status(404).json({ error: "Advance request not found" });
      }
      
      // Check if request is approved
      if (request.status !== "APPROVED") {
        return res.status(400).json({ error: "Cannot disburse unapproved advance request" });
      }
      
      // Check if already disbursed
      if (request.disbursementId) {
        return res.status(400).json({ error: "This request has already been disbursed" });
      }
      
      // Calculate fee (example: 2% of requested amount)
      const feeAmount = Math.round(request.requestedAmount * 0.02);
      const totalAmount = request.requestedAmount + feeAmount;
      
      // Create a payment intent with Stripe
      if (!stripe) {
        throw new Error("Stripe not initialized");
      }
      
      const paymentIntent = await stripe.paymentIntents.create({
        amount: totalAmount,
        currency: "usd",
        metadata: {
          advanceRequestId: request.id.toString(),
          employeeId: request.employeeId.toString(),
        },
      });
      
      // Create a disbursement record
      // Use raw SQL to create the disbursement with advanceRequestId
      const result = await pool.query(
        `INSERT INTO disbursements (
          advance_request_id, amount, fee_amount, total_amount, 
          payment_status, payment_method, stripe_payment_intent_id,
          disbursement_date, created_at, updated_at
        ) 
        VALUES ($1, $2, $3, $4, $5, $6, $7, NOW(), NOW(), NOW())
        RETURNING *`,
        [
          request.id, 
          request.requestedAmount, 
          feeAmount, 
          totalAmount, 
          "PROCESSING", 
          "STRIPE", 
          paymentIntent.id
        ]
      );
      
      const [disbursement] = result.rows;
      
      // Update the advance request with the disbursement ID
      await storage.updateAdvanceRequest(request.id, {
        disbursementId: disbursement.id,
        status: "DISBURSED",
      });
      
      return res.json({
        clientSecret: paymentIntent.client_secret,
        disbursement,
      });
    } catch (error) {
      console.error("Error creating payment intent:", error);
      return res.status(500).json({ error: "Failed to create payment intent" });
    }
  });
  
  // Webhook for Stripe events
  app.post("/api/webhooks/stripe", checkStripeAvailability, async (req: any, res: any) => {
    const signature = req.headers["stripe-signature"] as string;
    let event;
    
    try {
      if (!stripe) {
        throw new Error("Stripe not initialized");
      }
      
      // Verify the webhook signature
      event = stripe.webhooks.constructEvent(
        req.body,
        signature,
        process.env.STRIPE_WEBHOOK_SECRET || ""
      );
    } catch (error: any) {
      console.error("Webhook signature verification failed:", error);
      return res.status(400).send(`Webhook Error: ${error.message}`);
    }
    
    // Handle the event
    switch (event.type) {
      case "payment_intent.succeeded":
        const paymentIntent = event.data.object;
        // Update disbursement status
        if (paymentIntent.metadata.advanceRequestId) {
          const disbursements = await storage.getDisbursementsByAdvanceRequestId(
            parseInt(paymentIntent.metadata.advanceRequestId)
          );
          
          if (disbursements.length > 0) {
            const disbursement = disbursements[0];
            
            await storage.updateDisbursement(disbursement.id, {
              paymentStatus: "COMPLETED",
              transactionReference: paymentIntent.id,
            });
            
            // Create a notification
            const request = await storage.getAdvanceRequest(
              parseInt(paymentIntent.metadata.advanceRequestId)
            );
            
            if (request) {
              const employee = await storage.getEmployeeProfile(request.employeeId);
              
              if (employee) {
                await storage.createNotification({
                  userId: employee.userId,
                  title: "Advance Disbursed",
                  message: `Your salary advance of $${request.requestedAmount / 100} has been disbursed to your account.`,
                  type: "DISBURSEMENT",
                  relatedEntityId: disbursement.id,
                  relatedEntityType: "DISBURSEMENT",
                });
              }
            }
          }
        }
        break;
        
      case "payment_intent.payment_failed":
        const failedPaymentIntent = event.data.object;
        // Update disbursement status
        if (failedPaymentIntent.metadata.advanceRequestId) {
          const disbursements = await storage.getDisbursementsByAdvanceRequestId(
            parseInt(failedPaymentIntent.metadata.advanceRequestId)
          );
          
          if (disbursements.length > 0) {
            const disbursement = disbursements[0];
            
            await storage.updateDisbursement(disbursement.id, {
              paymentStatus: "FAILED",
              transactionReference: failedPaymentIntent.id,
            });
            
            // Revert the advance request status
            await storage.updateAdvanceRequest(parseInt(failedPaymentIntent.metadata.advanceRequestId), {
              status: "APPROVED",
              disbursementId: null,
            });
            
            // Create a notification
            const request = await storage.getAdvanceRequest(
              parseInt(failedPaymentIntent.metadata.advanceRequestId)
            );
            
            if (request) {
              const employee = await storage.getEmployeeProfile(request.employeeId);
              
              if (employee) {
                await storage.createNotification({
                  userId: employee.userId,
                  title: "Disbursement Failed",
                  message: `The disbursement of your salary advance of $${request.requestedAmount / 100} has failed. Please try again or contact support.`,
                  type: "DISBURSEMENT",
                  relatedEntityId: disbursement.id,
                  relatedEntityType: "DISBURSEMENT",
                });
              }
            }
          }
        }
        break;
    }
    
    res.json({ received: true });
  });
  
  // Notification routes
  
  // Get all notifications for the current user
  app.get("/api/notifications", async (req: any, res: any) => {
    try {
      if (!req.user) {
        return res.status(401).json({ error: "Authentication required" });
      }

      const notifications = await storage.getNotificationsByUserId(req.user.id);
      return res.json({ notifications });
    } catch (error) {
      console.error("Error fetching notifications:", error);
      return res.status(500).json({ error: "Failed to fetch notifications" });
    }
  });
  
  // Mark a notification as read
  app.patch("/api/notifications/:id/read", async (req: any, res: any) => {
    try {
      if (!req.user) {
        return res.status(401).json({ error: "Authentication required" });
      }

      const notificationId = parseInt(req.params.id);
      
      if (isNaN(notificationId)) {
        return res.status(400).json({ error: "Invalid notification ID" });
      }
      
      const notification = await storage.getNotification(notificationId);
      
      if (!notification) {
        return res.status(404).json({ error: "Notification not found" });
      }
      
      // Check if the notification belongs to the current user
      if (notification.userId !== req.user.id) {
        return res.status(403).json({ error: "Not authorized to update this notification" });
      }
      
      const updatedNotification = await storage.updateNotificationReadStatus(notificationId, true);
      return res.json({ notification: updatedNotification });
    } catch (error) {
      console.error("Error updating notification:", error);
      return res.status(500).json({ error: "Failed to update notification" });
    }
  });

  // Create HTTP server
  const httpServer = createServer(app);
  
  return httpServer;
}