import { cn } from "@/lib/utils";
import { statusTone } from "@/lib/format";

const tones: Record<string, string> = {
  success: "bg-success/15 text-success border-success/25",
  warning: "bg-warning/15 text-warning border-warning/25",
  danger: "bg-destructive/15 text-destructive border-destructive/25",
  info: "bg-primary/15 text-primary border-primary/25",
  neutral: "bg-muted text-muted-foreground border-border",
};

export function StatusBadge({ status }: { status: string }) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-medium",
        tones[statusTone(status)]
      )}
    >
      {status}
    </span>
  );
}
