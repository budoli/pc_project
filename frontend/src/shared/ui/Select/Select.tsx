type Option = {
  label: string;
  value: string;
};

type SelectProps = {
  label: string;
  options: Option[];
} & React.SelectHTMLAttributes<HTMLSelectElement>

const Select = ({
  label,
  options,
  className = "",
  ...props
}: SelectProps) => {
  return (
    <fieldset className="h-[60px] rounded-lg border border-gray-300 px-4">
      <legend className="px-1 sm text-gray-500">
        {label}
      </legend>
      <div className="flex h-[32px] items-center">
        <select
          className={`h-full w-full bg-transparent text-sm outline-none ${className}`}
          {...props}>
          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      </div>
    </fieldset>
  )
}

export default Select