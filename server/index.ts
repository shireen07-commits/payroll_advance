import express from "express";
import { registerRoutes } from "./routes";
import { setupAuth } from "./auth";

async function main() {
  // Create Express app
  const app = express();
  
  // JSON body parser
  app.use(express.json());
  
  // Set up authentication
  setupAuth(app);
  
  // Register API routes
  const httpServer = registerRoutes(app);
  
  // Start server
  const PORT = process.env.PORT || 3000;
  httpServer.listen(Number(PORT), "0.0.0.0", () => {
    console.log(`Server running at http://0.0.0.0:${PORT}`);
  });
}

main().catch((error) => {
  console.error("Failed to start server:", error);
  process.exit(1);
});