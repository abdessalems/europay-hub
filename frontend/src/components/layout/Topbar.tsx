import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Moon, Sun, LogOut } from "lucide-react";
import { http, unwrap } from "@/lib/api";
import type { Merchant } from "@/lib/types";
import { useAuth } from "@/auth/AuthContext";
import { Button } from "@/components/ui/button";

export function Topbar() {
  const { logout } = useAuth();
  const [dark, setDark] = useState(document.documentElement.classList.contains("dark"));

  const { data: merchant } = useQuery({
    queryKey: ["merchant"],
    queryFn: () => unwrap<Merchant>(http.get("/api/merchants/me")),
  });

  const toggleTheme = () => {
    const next = !dark;
    setDark(next);
    document.documentElement.classList.toggle("dark", next);
    localStorage.setItem("theme", next ? "dark" : "light");
  };

  return (
    <header className="flex h-16 items-center justify-between border-b border-border bg-background/80 px-6 backdrop-blur">
      <div>
        <p className="text-sm font-semibold">{merchant?.legalName ?? "Loading…"}</p>
        <p className="text-xs text-muted-foreground">{merchant?.email}</p>
      </div>
      <div className="flex items-center gap-2">
        <Button variant="ghost" size="icon" onClick={toggleTheme} aria-label="Toggle theme">
          {dark ? <Sun /> : <Moon />}
        </Button>
        <Button variant="outline" size="sm" onClick={logout}>
          <LogOut /> Sign out
        </Button>
      </div>
    </header>
  );
}
