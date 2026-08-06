import { cn } from "@/lib/utils";

/**
 * Brand-styled payment-method marks. These are lightweight wordmark chips (our own CSS/SVG,
 * no third-party asset files) used to identify each method — as a real checkout would.
 */

const base =
  "inline-flex items-center justify-center gap-1 rounded-md px-2 py-1 text-[11px] font-bold leading-none min-w-[74px] shadow-sm";

export function MethodLogo({ method, className }: { method: string; className?: string }) {
  switch (method) {
    case "VISA":
      return (
        <span className={cn(base, "italic tracking-wider text-white", className)} style={{ background: "#1434CB" }}>
          VISA
        </span>
      );
    case "BANCONTACT":
      return (
        <span className={cn(base, "text-white", className)} style={{ backgroundColor: "#004E9E" }}>
          <span aria-hidden className="size-2 rounded-full" style={{ background: "#FFD800" }} />
          Bancontact
        </span>
      );
    case "WERO":
      return (
        <span
          className={cn(base, "lowercase tracking-tight text-white", className)}
          style={{ background: "linear-gradient(135deg,#E5007D 0%,#8B2CF5 100%)" }}
        >
          wero
        </span>
      );
    default:
      return <span className={cn(base, "bg-muted text-foreground", className)}>{method}</span>;
  }
}
