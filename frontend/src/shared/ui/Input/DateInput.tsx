type DateInputProps = {
  label: string;
} & React.InputHTMLAttributes<HTMLInputElement>;

const DateInput = ({
  label,
  className = "",
  ...props
}: DateInputProps) => {
  return (
    <fieldset className={`h-[60px] rounded-lg border border-gray-300 px-4 ${className}`}>
      <legend className="px-1 text-sm text-gray-500">
        {label}
      </legend>

      <div className="flex h-[32px] items-center">
        <input
          type="date"
          className="w-full border-none bg-transparent text-sm outline-none "
          {...props}
        />
      </div>

    </fieldset>

  )
}

export default DateInput