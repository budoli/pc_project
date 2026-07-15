import FieldShell from "../Field/FieldShell";

type InputProps = {
  label: string;
  icon?: React.ReactNode
} & React.InputHTMLAttributes<HTMLInputElement>;

const Input = ({ label, icon, className = "", ...props }: InputProps) => {
  return (
    <FieldShell label={label} className={className}>
      <div className="flex w-full items-center gap-2">
        {icon && <span className="text-gray-400">{icon}</span>}

        <input
          className="sm w-full border-none bg-transparent outline-none placeholder:text-gray-400"
          {...props}
        />
      </div>
    </FieldShell>
  )
}

export default Input
