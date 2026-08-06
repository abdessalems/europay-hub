import { createContext, useContext, useMemo, useState, type ReactNode } from "react";
import { http, tokenStore, unwrap } from "@/lib/api";
import type { AuthResult } from "@/lib/types";

interface AuthContextValue {
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (legalName: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(tokenStore.get());

  const value = useMemo<AuthContextValue>(
    () => ({
      isAuthenticated: !!token,
      login: async (email, password) => {
        const result = await unwrap<AuthResult>(http.post("/api/auth/login", { email, password }));
        tokenStore.set(result.accessToken);
        setToken(result.accessToken);
      },
      register: async (legalName, email, password) => {
        await unwrap(http.post("/api/auth/register", { legalName, email, password }));
        const result = await unwrap<AuthResult>(http.post("/api/auth/login", { email, password }));
        tokenStore.set(result.accessToken);
        setToken(result.accessToken);
      },
      logout: () => {
        tokenStore.clear();
        setToken(null);
      },
    }),
    [token]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
