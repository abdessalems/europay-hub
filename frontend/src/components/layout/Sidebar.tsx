import { NavLink } from "react-router-dom";
import { LayoutDashboard, ShoppingCart, CreditCard, KeyRound, Users } from "lucide-react";
import { cn } from "@/lib/utils";

const nav = [
  { to: "/", label: "Dashboard", icon: LayoutDashboard, end: true },
  { to: "/orders", label: "Orders", icon: ShoppingCart },
  { to: "/payments", label: "Payments", icon: CreditCard },
  { to: "/customers", label: "Customers", icon: Users },
  { to: "/api-keys", label: "API Keys", icon: KeyRound },
];

export function Sidebar() {
  return (
    <aside className="hidden md:flex w-64 shrink-0 flex-col bg-sidebar text-sidebar-foreground">
      <div className="flex h-16 items-center gap-2 px-6 text-lg font-extrabold text-white">
        <span className="text-2xl">💶</span>
        <span>EuroPay<span className="text-primary"> Hub</span></span>
      </div>
      <nav className="flex-1 space-y-1 px-3 py-4">
        {nav.map(({ to, label, icon: Icon, end }) => (
          <NavLink
            key={to}
            to={to}
            end={end}
            className={({ isActive }) =>
              cn(
                "flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors",
                isActive
                  ? "bg-primary text-primary-foreground shadow-sm shadow-primary/30"
                  : "text-sidebar-foreground/80 hover:bg-white/5 hover:text-white"
              )
            }
          >
            <Icon className="size-4" />
            {label}
          </NavLink>
        ))}
      </nav>
      <div className="px-6 py-4 text-xs text-sidebar-foreground/50">
        Merchant Console · v0.1
      </div>
    </aside>
  );
}
