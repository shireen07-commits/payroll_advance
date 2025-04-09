import { eq, and } from "drizzle-orm";
import { db } from "./db";
import {
  users,
  employeeProfiles,
  employerProfiles,
  advanceRequests,
  disbursements,
  disbursementsRelations,
  repayments,
  notifications,
  type User,
  type InsertUser,
  type EmployeeProfile,
  type InsertEmployeeProfile,
  type EmployerProfile,
  type InsertEmployerProfile,
  type AdvanceRequest,
  type InsertAdvanceRequest,
  type Disbursement,
  type InsertDisbursement,
  type Repayment,
  type InsertRepayment,
  type Notification,
  type InsertNotification
} from "../shared/schema";
import session from "express-session";
import connectPg from "connect-pg-simple";
import { Pool } from "pg";

// Import the pool for the session store and queries
import { pool } from "./db";
const dbPool = pool;

const PostgresSessionStore = connectPg(session);

// Storage interface for all database operations
export interface IStorage {
  // User operations
  getUser(id: number): Promise<User | undefined>;
  getUserByEmail(email: string): Promise<User | undefined>;
  createUser(user: InsertUser): Promise<User>;
  updateUser(id: number, user: Partial<InsertUser>): Promise<User | undefined>;
  
  // Employee profile operations
  getEmployeeProfile(id: number): Promise<EmployeeProfile | undefined>;
  getEmployeeProfileByUserId(userId: number): Promise<EmployeeProfile | undefined>;
  createEmployeeProfile(profile: InsertEmployeeProfile): Promise<EmployeeProfile>;
  updateEmployeeProfile(id: number, profile: Partial<InsertEmployeeProfile>): Promise<EmployeeProfile | undefined>;
  
  // Employer profile operations
  getEmployerProfile(id: number): Promise<EmployerProfile | undefined>;
  getEmployerProfileByUserId(userId: number): Promise<EmployerProfile | undefined>;
  createEmployerProfile(profile: InsertEmployerProfile): Promise<EmployerProfile>;
  updateEmployerProfile(id: number, profile: Partial<InsertEmployerProfile>): Promise<EmployerProfile | undefined>;
  
  // Advance request operations
  getAdvanceRequest(id: number): Promise<AdvanceRequest | undefined>;
  getAdvanceRequestsByEmployeeId(employeeId: number): Promise<AdvanceRequest[]>;
  createAdvanceRequest(request: InsertAdvanceRequest): Promise<AdvanceRequest>;
  updateAdvanceRequest(id: number, request: Partial<InsertAdvanceRequest>): Promise<AdvanceRequest | undefined>;
  
  // Disbursement operations
  getDisbursement(id: number): Promise<Disbursement | undefined>;
  getDisbursementsByAdvanceRequestId(advanceRequestId: number): Promise<Disbursement[]>;
  createDisbursement(disbursement: InsertDisbursement): Promise<Disbursement>;
  updateDisbursement(id: number, disbursement: Partial<InsertDisbursement>): Promise<Disbursement | undefined>;
  
  // Repayment operations
  getRepayment(id: number): Promise<Repayment | undefined>;
  getRepaymentsByDisbursementId(disbursementId: number): Promise<Repayment[]>;
  createRepayment(repayment: InsertRepayment): Promise<Repayment>;
  updateRepayment(id: number, repayment: Partial<InsertRepayment>): Promise<Repayment | undefined>;
  
  // Notification operations
  getNotification(id: number): Promise<Notification | undefined>;
  getNotificationsByUserId(userId: number): Promise<Notification[]>;
  createNotification(notification: InsertNotification): Promise<Notification>;
  updateNotificationReadStatus(id: number, isRead: boolean): Promise<Notification | undefined>;
  
  // Session store
  sessionStore: session.Store;
}

// Database storage implementation
export class DatabaseStorage implements IStorage {
  sessionStore: session.Store;
  
  constructor() {
    // Initialize the PostgreSQL session store
    this.sessionStore = new PostgresSessionStore({
      pool: dbPool as any, // Type cast to avoid compatibility issues
      createTableIfMissing: true
    });
  }
  
  // User operations
  async getUser(id: number): Promise<User | undefined> {
    const [user] = await db.select().from(users).where(eq(users.id, id));
    return user;
  }
  
  async getUserByEmail(email: string): Promise<User | undefined> {
    const [user] = await db.select().from(users).where(eq(users.email, email));
    return user;
  }
  
  async createUser(user: InsertUser): Promise<User> {
    const [newUser] = await db.insert(users).values(user).returning();
    return newUser;
  }
  
  async updateUser(id: number, user: Partial<InsertUser>): Promise<User | undefined> {
    const [updatedUser] = await db
      .update(users)
      .set({ ...user, updatedAt: new Date() })
      .where(eq(users.id, id))
      .returning();
    return updatedUser;
  }
  
  // Employee profile operations
  async getEmployeeProfile(id: number): Promise<EmployeeProfile | undefined> {
    const [profile] = await db.select().from(employeeProfiles).where(eq(employeeProfiles.id, id));
    return profile;
  }
  
  async getEmployeeProfileByUserId(userId: number): Promise<EmployeeProfile | undefined> {
    const [profile] = await db.select().from(employeeProfiles).where(eq(employeeProfiles.userId, userId));
    return profile;
  }
  
  async createEmployeeProfile(profile: InsertEmployeeProfile): Promise<EmployeeProfile> {
    const [newProfile] = await db.insert(employeeProfiles).values(profile).returning();
    return newProfile;
  }
  
  async updateEmployeeProfile(id: number, profile: Partial<InsertEmployeeProfile>): Promise<EmployeeProfile | undefined> {
    const [updatedProfile] = await db
      .update(employeeProfiles)
      .set({ ...profile, updatedAt: new Date() })
      .where(eq(employeeProfiles.id, id))
      .returning();
    return updatedProfile;
  }
  
  // Employer profile operations
  async getEmployerProfile(id: number): Promise<EmployerProfile | undefined> {
    const [profile] = await db.select().from(employerProfiles).where(eq(employerProfiles.id, id));
    return profile;
  }
  
  async getEmployerProfileByUserId(userId: number): Promise<EmployerProfile | undefined> {
    const [profile] = await db.select().from(employerProfiles).where(eq(employerProfiles.userId, userId));
    return profile;
  }
  
  async createEmployerProfile(profile: InsertEmployerProfile): Promise<EmployerProfile> {
    const [newProfile] = await db.insert(employerProfiles).values(profile).returning();
    return newProfile;
  }
  
  async updateEmployerProfile(id: number, profile: Partial<InsertEmployerProfile>): Promise<EmployerProfile | undefined> {
    const [updatedProfile] = await db
      .update(employerProfiles)
      .set({ ...profile, updatedAt: new Date() })
      .where(eq(employerProfiles.id, id))
      .returning();
    return updatedProfile;
  }
  
  // Advance request operations
  async getAdvanceRequest(id: number): Promise<AdvanceRequest | undefined> {
    const [request] = await db.select().from(advanceRequests).where(eq(advanceRequests.id, id));
    return request;
  }
  
  async getAdvanceRequestsByEmployeeId(employeeId: number): Promise<AdvanceRequest[]> {
    return await db.select().from(advanceRequests).where(eq(advanceRequests.employeeId, employeeId));
  }
  
  async createAdvanceRequest(request: InsertAdvanceRequest): Promise<AdvanceRequest> {
    const [newRequest] = await db.insert(advanceRequests).values(request).returning();
    return newRequest;
  }
  
  async updateAdvanceRequest(id: number, request: Partial<InsertAdvanceRequest>): Promise<AdvanceRequest | undefined> {
    const [updatedRequest] = await db
      .update(advanceRequests)
      .set({ ...request, updatedAt: new Date() })
      .where(eq(advanceRequests.id, id))
      .returning();
    return updatedRequest;
  }
  
  // Disbursement operations
  async getDisbursement(id: number): Promise<Disbursement | undefined> {
    const [disbursement] = await db.select().from(disbursements).where(eq(disbursements.id, id));
    return disbursement;
  }
  
  async getDisbursementsByAdvanceRequestId(advanceRequestId: number): Promise<Disbursement[]> {
    // Use raw SQL query through pool
    const result = await pool.query(
      `SELECT * FROM disbursements WHERE advance_request_id = $1`, 
      [advanceRequestId]
    );
    
    return result.rows as Disbursement[];
  }
  
  async createDisbursement(disbursement: InsertDisbursement): Promise<Disbursement> {
    const [newDisbursement] = await db.insert(disbursements).values(disbursement).returning();
    return newDisbursement;
  }
  
  async updateDisbursement(id: number, disbursement: Partial<InsertDisbursement>): Promise<Disbursement | undefined> {
    const [updatedDisbursement] = await db
      .update(disbursements)
      .set({ ...disbursement, updatedAt: new Date() })
      .where(eq(disbursements.id, id))
      .returning();
    return updatedDisbursement;
  }
  
  // Repayment operations
  async getRepayment(id: number): Promise<Repayment | undefined> {
    const [repayment] = await db.select().from(repayments).where(eq(repayments.id, id));
    return repayment;
  }
  
  async getRepaymentsByDisbursementId(disbursementId: number): Promise<Repayment[]> {
    return await db.select().from(repayments).where(eq(repayments.disbursementId, disbursementId));
  }
  
  async createRepayment(repayment: InsertRepayment): Promise<Repayment> {
    const [newRepayment] = await db.insert(repayments).values(repayment).returning();
    return newRepayment;
  }
  
  async updateRepayment(id: number, repayment: Partial<InsertRepayment>): Promise<Repayment | undefined> {
    const [updatedRepayment] = await db
      .update(repayments)
      .set({ ...repayment, updatedAt: new Date() })
      .where(eq(repayments.id, id))
      .returning();
    return updatedRepayment;
  }
  
  // Notification operations
  async getNotification(id: number): Promise<Notification | undefined> {
    const [notification] = await db.select().from(notifications).where(eq(notifications.id, id));
    return notification;
  }
  
  async getNotificationsByUserId(userId: number): Promise<Notification[]> {
    return await db.select().from(notifications).where(eq(notifications.userId, userId));
  }
  
  async createNotification(notification: InsertNotification): Promise<Notification> {
    const [newNotification] = await db.insert(notifications).values(notification).returning();
    return newNotification;
  }
  
  async updateNotificationReadStatus(id: number, isRead: boolean): Promise<Notification | undefined> {
    const [updatedNotification] = await db
      .update(notifications)
      .set({ isRead })
      .where(eq(notifications.id, id))
      .returning();
    return updatedNotification;
  }
}

// Export the database storage instance
export const storage = new DatabaseStorage();