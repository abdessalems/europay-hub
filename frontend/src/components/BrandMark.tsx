/** EuroPay Hub emblem — a blue globe with gold stars and an orange € coin (our own SVG). */
export function BrandMark({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 64 64" className={className} role="img" aria-label="EuroPay Hub" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <radialGradient id="bm-globe" cx="42%" cy="35%" r="75%">
          <stop offset="0%" stopColor="#4a90d9" />
          <stop offset="60%" stopColor="#1e5aa8" />
          <stop offset="100%" stopColor="#0e3f7e" />
        </radialGradient>
        <linearGradient id="bm-coin" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#ffcb52" />
          <stop offset="100%" stopColor="#f08a1d" />
        </linearGradient>
      </defs>
      <ellipse cx="27" cy="33" rx="26" ry="10.5" transform="rotate(-27 27 33)" fill="none" stroke="#F5A623" strokeWidth="3" />
      <circle cx="27" cy="33" r="17" fill="url(#bm-globe)" />
      <g stroke="#bcd8f5" strokeWidth="1.2" fill="none" opacity="0.75">
        <ellipse cx="27" cy="33" rx="7" ry="17" />
        <line x1="10" y1="33" x2="44" y2="33" />
        <line x1="13" y1="24" x2="41" y2="24" />
        <line x1="13" y1="42" x2="41" y2="42" />
      </g>
      <g fill="#FBC02D">
        <path d="M10,11l1,2.4 2.6,.2-2,1.7 .7,2.5-2.3-1.4-2.3,1.4 .7-2.5-2-1.7 2.6-.2z" />
        <path d="M21,6l1.1,2.7 2.9,.2-2.2,1.9 .7,2.8-2.5-1.5-2.5,1.5 .7-2.8-2.2-1.9 2.9-.2z" />
      </g>
      <circle cx="47" cy="46" r="13.5" fill="url(#bm-coin)" stroke="#d97706" />
      <text x="47" y="52" fontSize="17" fontWeight="700" textAnchor="middle" fill="#fff"
        fontFamily="Inter, Arial, sans-serif">€</text>
    </svg>
  );
}
