import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { formatMoney } from "@/lib/format";

const ROSE = "hsl(347 77% 52%)";

function ChartTooltip({ active, payload, label }: any) {
  if (!active || !payload?.length) return null;
  return (
    <div className="rounded-lg border border-border bg-card px-3 py-2 text-sm shadow-md">
      <p className="text-muted-foreground">{label}</p>
      <p className="font-semibold">{formatMoney(payload[0].value)}</p>
    </div>
  );
}

export function RevenueAreaChart({ data }: { data: { label: string; value: number }[] }) {
  return (
    <ResponsiveContainer width="100%" height={280}>
      <AreaChart data={data} margin={{ top: 10, right: 8, left: -8, bottom: 0 }}>
        <defs>
          <linearGradient id="revFill" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor={ROSE} stopOpacity={0.35} />
            <stop offset="100%" stopColor={ROSE} stopOpacity={0} />
          </linearGradient>
        </defs>
        <CartesianGrid strokeDasharray="3 3" stroke="hsl(240 4% 50% / 0.15)" vertical={false} />
        <XAxis dataKey="label" tickLine={false} axisLine={false} className="text-muted-foreground"
          tick={{ fontSize: 12, fill: "currentColor" }} minTickGap={20} />
        <YAxis tickLine={false} axisLine={false} width={56} className="text-muted-foreground"
          tick={{ fontSize: 12, fill: "currentColor" }} tickFormatter={(v) => `€${v}`} />
        <Tooltip content={<ChartTooltip />} />
        <Area type="monotone" dataKey="value" stroke={ROSE} strokeWidth={2.5} fill="url(#revFill)" />
      </AreaChart>
    </ResponsiveContainer>
  );
}
