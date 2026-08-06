import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";
import { METHOD_COLORS } from "@/lib/analytics";
import { MethodLogo } from "@/components/MethodLogo";

function DonutTooltip({ active, payload }: any) {
  if (!active || !payload?.length) return null;
  const p = payload[0];
  return (
    <div className="rounded-lg border border-border bg-card px-3 py-2 text-sm shadow-md">
      <span className="inline-flex items-center gap-1.5">
        <span className="size-2.5 rounded-full" style={{ background: METHOD_COLORS[p.name] ?? "gray" }} />
        <span className="font-medium text-foreground">{p.name}</span>
        <span className="text-muted-foreground">· {p.value}</span>
      </span>
    </div>
  );
}

export function MethodDonut({ data }: { data: { name: string; value: number }[] }) {
  const total = data.reduce((s, d) => s + d.value, 0);
  return (
    <div className="flex items-center gap-6">
      <div className="relative h-[180px] w-[180px] shrink-0">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie data={data} dataKey="value" nameKey="name" innerRadius={58} outerRadius={82}
              paddingAngle={3} stroke="none">
              {data.map((d) => (
                <Cell key={d.name} fill={METHOD_COLORS[d.name] ?? "hsl(240 4% 60%)"} />
              ))}
            </Pie>
            <Tooltip content={<DonutTooltip />} />

          </PieChart>
        </ResponsiveContainer>
        <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center">
          <span className="text-2xl font-bold">{total}</span>
          <span className="text-xs text-muted-foreground">payments</span>
        </div>
      </div>
      <ul className="space-y-2 text-sm">
        {data.map((d) => (
          <li key={d.name} className="flex items-center gap-2">
            <MethodLogo method={d.name} />
            <span className="text-muted-foreground">· {d.value}</span>
          </li>
        ))}
        {data.length === 0 && <li className="text-muted-foreground">No data yet</li>}
      </ul>
    </div>
  );
}
