import { pgTable, serial, text, timestamp, boolean, integer, pgEnum } from "drizzle-orm/pg-core";
import { createInsertSchema } from "drizzle-zod";
import { z } from "zod";

// Enums
export const userRoleEnum = pgEnum("user_role", ["EMPLOYEE", "EMPLOYER", "ADMIN"]);
export const kycStatusEnum = pgEnum("kyc_status", ["NOT_STARTED", "IN_PROGRESS", "VERIFIED", "REJECTED"]);
export const paymentStatusEnum = pgEnum("payment_status", ["PENDING", "PROCESSING", "COMPLETED", "FAILED"]);
export const advanceRequestStatusEnum = pgEnum("advance_request_status", ["PENDING", "APPROVED", "REJECTED", "DISBURSED"]);

// Users table
export const users = pgTable("users", {
  id: serial("id").primaryKey(),
  email: text("email").notNull().unique(),
  passwordHash: text("password_hash").notNull(),
  firstName: text("first_name").notNull(),
  lastName: text("last_name").notNull(),
  role: userRoleEnum("role").notNull().default("EMPLOYEE"),
  kycStatus: kycStatusEnum("kyc_status").notNull().default("NOT_STARTED"),
  phoneNumber: text("phone_number"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
  lastLoginAt: timestamp("last_login_at"),
  active: boolean("active").default(true).notNull(),
});

// Employer profiles table - defined before employee profiles due to reference
export const employerProfiles = pgTable("employer_profiles", {
  id: serial("id").primaryKey(),
  userId: integer("user_id").notNull().references(() => users.id),
  companyName: text("company_name").notNull(),
  companyRegistrationNumber: text("company_registration_number"),
  industry: text("industry"),
  contactPersonName: text("contact_person_name"),
  contactEmail: text("contact_email"),
  contactPhoneNumber: text("contact_phone_number"),
  companyAddress: text("company_address"),
  defaultMaxAdvancePercentage: integer("default_max_advance_percentage").default(30),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

// Employee profiles table
export const employeeProfiles = pgTable("employee_profiles", {
  id: serial("id").primaryKey(),
  userId: integer("user_id").notNull().references(() => users.id),
  employerId: integer("employer_id").references(() => employerProfiles.id),
  employeeId: text("employee_id").notNull(), // Internal employee ID in employer's system
  position: text("position"),
  department: text("department"),
  hireDate: timestamp("hire_date"),
  salary: integer("salary").notNull(), // Monthly salary in cents
  payFrequency: text("pay_frequency").notNull().default("MONTHLY"), // MONTHLY, BI_WEEKLY, WEEKLY
  bankAccountNumber: text("bank_account_number"),
  bankRoutingNumber: text("bank_routing_number"),
  bankName: text("bank_name"),
  maxAdvancePercentage: integer("max_advance_percentage").default(30), // Maximum % of salary that can be advanced
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

// Disbursements table - forward declaration to break circular dependency
export const disbursements = pgTable("disbursements", {
  id: serial("id").primaryKey(),
  // advanceRequestId will be added after advanceRequests is defined
  amount: integer("amount").notNull(), // In cents
  feeAmount: integer("fee_amount").notNull().default(0), // In cents
  totalAmount: integer("total_amount").notNull(), // amount + feeAmount
  paymentStatus: paymentStatusEnum("payment_status").notNull().default("PENDING"),
  paymentMethod: text("payment_method").notNull(),
  transactionReference: text("transaction_reference"),
  stripePaymentIntentId: text("stripe_payment_intent_id"),
  disbursementDate: timestamp("disbursement_date").defaultNow().notNull(),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

// Advance requests table
export const advanceRequests = pgTable("advance_requests", {
  id: serial("id").primaryKey(),
  employeeId: integer("employee_id").notNull().references(() => employeeProfiles.id),
  requestedAmount: integer("requested_amount").notNull(), // In cents
  reason: text("reason"),
  status: advanceRequestStatusEnum("status").notNull().default("PENDING"),
  requestDate: timestamp("request_date").defaultNow().notNull(),
  approvedBy: integer("approved_by").references(() => users.id),
  approvedDate: timestamp("approved_date"),
  disbursementId: integer("disbursement_id").references(() => disbursements.id),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

// Add the reference to advanceRequests after it's defined (adds a foreign key constraint)
// This is a workaround for circular references
export const disbursementsRelations = {
  advanceRequestId: integer("advance_request_id").references(() => advanceRequests.id),
};

// Repayments table
export const repayments = pgTable("repayments", {
  id: serial("id").primaryKey(),
  disbursementId: integer("disbursement_id").notNull().references(() => disbursements.id),
  amount: integer("amount").notNull(), // In cents
  paymentStatus: paymentStatusEnum("payment_status").notNull().default("PENDING"),
  dueDate: timestamp("due_date").notNull(),
  paymentDate: timestamp("payment_date"),
  paymentMethod: text("payment_method"),
  transactionReference: text("transaction_reference"),
  stripePaymentIntentId: text("stripe_payment_intent_id"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

// Notifications table
export const notifications = pgTable("notifications", {
  id: serial("id").primaryKey(),
  userId: integer("user_id").notNull().references(() => users.id),
  title: text("title").notNull(),
  message: text("message").notNull(),
  isRead: boolean("is_read").default(false).notNull(),
  type: text("type").notNull(), // ADVANCE_REQUEST, APPROVAL, DISBURSEMENT, REPAYMENT, SYSTEM
  relatedEntityId: integer("related_entity_id"), // ID of the related entity (advance request, disbursement, etc.)
  relatedEntityType: text("related_entity_type"), // Type of the related entity
  createdAt: timestamp("created_at").defaultNow().notNull(),
});

// Define types for each table
export type User = typeof users.$inferSelect;
export type InsertUser = typeof users.$inferInsert;
export const insertUserSchema = createInsertSchema(users).omit({ id: true });

export type EmployeeProfile = typeof employeeProfiles.$inferSelect;
export type InsertEmployeeProfile = typeof employeeProfiles.$inferInsert;
export const insertEmployeeProfileSchema = createInsertSchema(employeeProfiles).omit({ id: true });

export type EmployerProfile = typeof employerProfiles.$inferSelect;
export type InsertEmployerProfile = typeof employerProfiles.$inferInsert;
export const insertEmployerProfileSchema = createInsertSchema(employerProfiles).omit({ id: true });

export type AdvanceRequest = typeof advanceRequests.$inferSelect;
export type InsertAdvanceRequest = typeof advanceRequests.$inferInsert;
export const insertAdvanceRequestSchema = createInsertSchema(advanceRequests).omit({ id: true });

export type Disbursement = typeof disbursements.$inferSelect;
export type InsertDisbursement = typeof disbursements.$inferInsert;
export const insertDisbursementSchema = createInsertSchema(disbursements).omit({ id: true });

export type Repayment = typeof repayments.$inferSelect;
export type InsertRepayment = typeof repayments.$inferInsert;
export const insertRepaymentSchema = createInsertSchema(repayments).omit({ id: true });

export type Notification = typeof notifications.$inferSelect;
export type InsertNotification = typeof notifications.$inferInsert;
export const insertNotificationSchema = createInsertSchema(notifications).omit({ id: true });