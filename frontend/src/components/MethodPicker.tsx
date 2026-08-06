import { useState } from "react";
import { ChevronDown } from "lucide-react";
import type { PaymentMethod } from "@/lib/types";
import { PAYMENT_METHODS } from "@/lib/methods";
import { Button } from "@/components/ui/button";
import { MethodLogo } from "@/components/MethodLogo";

/** A compact "Pay ▾" dropdown listing every payment method with its brand mark. */
export function MethodPicker({
  disabled,
  onPick,
}: {
  disabled?: boolean;
  onPick: (method: PaymentMethod) => void;
}) {
  const [open, setOpen] = useState(false);

  return (
    <div className="relative inline-block text-left">
      <Button size="sm" disabled={disabled} onClick={() => setOpen((o) => !o)}>
        Pay <ChevronDown />
      </Button>
      {open && (
        <>
          <div className="fixed inset-0 z-10" onClick={() => setOpen(false)} />
          <div className="absolute right-0 z-20 mt-1 w-52 rounded-xl border border-border bg-card p-1.5 shadow-xl">
            <p className="px-2 py-1 text-xs font-medium text-muted-foreground">Choose a method</p>
            {PAYMENT_METHODS.map((m) => (
              <button
                key={m}
                type="button"
                className="flex w-full items-center rounded-lg px-2 py-1.5 transition-colors hover:bg-accent"
                onClick={() => { onPick(m); setOpen(false); }}
              >
                <MethodLogo method={m} />
              </button>
            ))}
          </div>
        </>
      )}
    </div>
  );
}
