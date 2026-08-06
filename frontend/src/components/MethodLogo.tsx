import { cn } from "@/lib/utils";

/**
 * Brand-styled payment-method marks. These are lightweight wordmark chips (our own CSS/SVG,
 * no third-party asset files) used to identify each method — as a real checkout would.
 */

const base =
  "inline-flex w-[118px] shrink-0 items-center justify-center gap-1 whitespace-nowrap rounded-md px-2 py-1 text-[11px] font-bold leading-none shadow-sm";

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
    case "MASTERCARD":
      return (
        <span className={cn(base, "text-white", className)} style={{ backgroundColor: "#16181D" }}>
          <span aria-hidden className="relative inline-flex items-center" style={{ width: 17 }}>
            <span className="size-3 rounded-full" style={{ background: "#EB001B" }} />
            <span className="-ml-1.5 size-3 rounded-full" style={{ background: "#F79E1B", mixBlendMode: "screen" }} />
          </span>
          Mastercard
        </span>
      );
    case "SEPA_INSTANT":
      return (
        <span className={cn(base, "text-white", className)} style={{ backgroundColor: "#0E9488" }}>
          SEPA <span aria-hidden>⚡</span>
        </span>
      );
    case "PAYPAL":
      return (
        <span className={cn(base, "italic", className)} style={{ background: "#fff", border: "1px solid #dbe3ef" }}>
          <span style={{ color: "#003087" }}>Pay</span>
          <span style={{ color: "#009CDE" }}>Pal</span>
        </span>
      );
    case "APPLE_PAY":
      return (
        <span className={cn(base, "gap-1 text-white", className)} style={{ backgroundColor: "#000" }}>
          <svg width="11" height="13" viewBox="0 0 14 17" fill="currentColor" aria-hidden>
            <path d="M11.2 9c0-1.6 1.3-2.4 1.4-2.5-.8-1.1-2-1.3-2.4-1.3-1-.1-2 .6-2.5.6s-1.3-.6-2.2-.6c-1.1 0-2.2.7-2.7 1.7-1.2 2-.3 5 .8 6.6.6.8 1.2 1.7 2.1 1.6.8 0 1.1-.5 2.1-.5s1.3.5 2.2.5c.9 0 1.5-.8 2-1.6.7-.9.9-1.8 1-1.9-.1 0-1.8-.7-1.8-2.7zM9.6 3.9c.4-.5.7-1.3.6-2-.6 0-1.4.4-1.8.9-.4.4-.8 1.2-.7 1.9.7.1 1.4-.3 1.9-.8z"/>
          </svg>
          Pay
        </span>
      );
    default:
      return <span className={cn(base, "bg-muted text-foreground", className)}>{method}</span>;
  }
}
