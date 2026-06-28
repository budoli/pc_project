type ButtonProps = {
    children: React.ReactNode;
    variant?: "primary" | "secondary" | "google" | "kakao";
    size?: "sm" | "md" | "lg" | "input";
    className?: string;
} & React.ButtonHTMLAttributes<HTMLButtonElement>;

const Button = ({
    children,
    variant = "primary",
    size = "lg",
    className = "",
    ...props
}: ButtonProps) => {
    const variants = {
        primary: "bg-blue-100 text-white",
        secondary: "border border-gray-300 bg-white",
        google: "border border-gray-300 bg-white",
        kakao: "bg-[#FEE500] text-black",
    };

    const sizes = {
        sm: "h-8 w-auto px-3 rounded-lg text-xs",
        md: "h-10 w-auto px-4 rounded-lg text-sm",
        lg: "h-12 w-full px-4 rounded-lg text-sm",
        input: "h-full px-6 rounded-none border-l border-gray-300 text-sm",
    };

    return (
        <button
            className={`
            ${variants[variant]}
            ${sizes[size]}
            font-medium
            transition-colors
            ${className}
            `}
            {...props}
        >
            {children}
        </button>
    );
};

export default Button;
