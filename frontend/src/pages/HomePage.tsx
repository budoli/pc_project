import { useState } from "react"
import LoginModal from "../features/auth/LoginModal"

const HomePage = () => {
  const [open, SetOpen] = useState(true);

  return (
    <>
      <button
        onClick={() => SetOpen(true)}
        className=""
      >
        로그인
      </button>

      {open && <LoginModal onClose={() => SetOpen(false)} />}
    </>
  )
}

export default HomePage