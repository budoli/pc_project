type FieldShellProps = {
  label: string;
  children: React.ReactNode;
  className?: string;
};

const FieldShell = ({ label, children, className = "" }: FieldShellProps) => {
  return (
    <fieldset className={`h-[60px] rounded-lg border border-gray-300 px-4 ${className}`}>
      <legend className="sm px-1 text-gray-500">
        {label}
      </legend>

      <div className="flex h-[32px] items-center">
        {children}
      </div>
    </fieldset>
  );
};

export default FieldShell;
