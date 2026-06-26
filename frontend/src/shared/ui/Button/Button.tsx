type ButtonProps = {
  children: React.ReactNode;
  variant?: "primary" | "secondary" | "google" | "kakao";
  className?: string;
} & React.ButtonHTMLAttributes<HTMLButtonElement>;

const Button = ({
  children,
  variant = 'primary',
  className = '',
  ...props
}: ButtonProps) => {
  const variants = {
    primary : "bg-blue-100 text-white",
    secondary : "border border-gray-300 bg-white",
    google : "border border-gray-300 bg-white",
    kakao : "bg-[#FEE500] text-black",
  }

  return (
    <button
      className={`h-12 w-full rounded-lg ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>

  )
}

export default Button