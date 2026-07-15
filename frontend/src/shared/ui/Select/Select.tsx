import FieldShell from "../Field/FieldShell";

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
    <FieldShell label={label}>
      <select
        className={`sm h-full w-full bg-transparent outline-none ${className}`}
        {...props}>
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </FieldShell>
  )
}

export default Select
