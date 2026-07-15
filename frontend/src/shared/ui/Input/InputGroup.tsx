type InputGroupProps = {
  label: string;
  icon?: React.ReactNode;
  button?: React.ReactNode;
  helperText?: React.ReactNode;
} & React.InputHTMLAttributes<HTMLInputElement>;

const InputGroup = ({
  label,
  icon,
  button,
  helperText,
  className = "",
  ...props
}: InputGroupProps) => {
  return (
    <div className={`flex flex-col gap-1 ${className}`}>
      <label className="sm text-gray-500">
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
            className="sm w-full border-none bg-transparent outline-none placeholder:text-gray-400"
            {...props}
          />
        </div>

        {button && (
          <div className="shrink-0 border-l border-gray-300">
            {button}
          </div>
        )}
      </div>
      {helperText && (
        <div className="sm text-gray-500">
      {helperText}
        </div>
      )}
    </div>
  );
};

export default InputGroup;
