type InputProps = {
  label: string;
  icon?: React.ReactNode
} & React.InputHTMLAttributes<HTMLInputElement>;

const Input = ({ label, icon, className = "", ...props }: InputProps) => {
  return (
    <fieldset className={`h-[60px] rounded-lg border border-gray-300 px-4 ${className}`}>
      <legend className="px-1 text-sm text-gray-500">
        {label}
      </legend>

      <div className="flex h-[32px] items-center gap-2">
        {icon && <span className="text-gray-400">{icon}</span>}

        <input
          className="w-full border-none bg-transparent text-sm outline-none placeholder:text-gray-400"
          {...props}
        />
      </div>
    </fieldset>

  )
}

export default Input