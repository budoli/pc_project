type InputGroupProps = {
  label: string;
  icon?: React.ReactNode;
  button?: React.ReactNode;
} & React.InputHTMLAttributes<HTMLInputElement>;

const InputGroup = ({
  label,
  icon,
  button,
  className = "",
  ...props
}: InputGroupProps) => {
  return (
    <div className={`flex flex-col gap-1 ${className}`}>
      <label className="text-sm text-gray-500">
        {label}
      </label>

      <div className="flex h-12 overflow-hidden rounded-lg border border-gray-300">
        <div className="flex flex-1 items-center px-4">
          {icon && (
            <span className="mr-2 text-gray-400">
              {icon}
            </span>
          )}

          <input
            className="w-full border-none bg-transparent text-sm outline-none placeholder:text-gray-400"
            {...props}
          />
        </div>

        {button}
      </div>
    </div>
  );
};

export default InputGroup;