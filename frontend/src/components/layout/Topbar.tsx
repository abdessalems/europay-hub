import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Moon, Sun, LogOut } from "lucide-react";
import { http, unwrap } from "@/lib/api";
import type { Merchant } from "@/lib/types";
import { useAuth } from "@/auth/AuthContext";
import { Button } from "@/components/ui/button";
import { BrandMark } from "@/components/BrandMark";

function initials(name?: string) {
  if (!name) return "··";
  return name.trim().split(/\s+/).slice(0, 2).map((w) => w[0]?.toUpperCase()).join("");
}

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
    <header className="sticky top-0 z-10 flex h-16 items-center justify-between border-b border-border bg-background/70 px-6 backdrop-blur-md">
      <div className="flex items-center gap-2 md:hidden text-base font-extrabold">
        <BrandMark className="size-7" /> EuroPay<span className="text-primary">Hub</span>
      </div>
      <div className="hidden md:block">
        <p className="text-sm font-semibold leading-tight">{merchant?.legalName ?? "Loading…"}</p>
        <p className="text-xs text-muted-foreground">{merchant?.email}</p>
      </div>

      <div className="flex items-center gap-2">
        <Button variant="ghost" size="icon" onClick={toggleTheme} aria-label="Toggle theme">
          {dark ? <Sun /> : <Moon />}
        </Button>
        <div className="mx-1 hidden items-center gap-2 rounded-full border border-border py-1 pl-1 pr-3 sm:flex">
          <span className="grid size-8 place-items-center rounded-full bg-primary text-xs font-bold text-primary-foreground">
            {initials(merchant?.legalName)}
          </span>
          <span className="text-xs font-medium text-muted-foreground">Merchant</span>
        </div>
        <Button variant="outline" size="sm" onClick={logout}>
          <LogOut /> Sign out
        </Button>
      </div>
    </header>
  );
}
