import FieldShell from "../Field/FieldShell";

type DateInputProps = {
  label: string;
} & React.InputHTMLAttributes<HTMLInputElement>;

const DateInput = ({
  label,
  className = "",
  ...props
}: DateInputProps) => {
  return (
    <FieldShell label={label} className={className}>
      <input
        type="date"
        className="sm w-full border-none bg-transparent outline-none"
        {...props}
      />
    </FieldShell>
  )
}

export default DateInput
