import ChatInput from "@/shared/ui/Input/ChatInput";
import { useNavigate } from "react-router-dom";

const HomePage = () => {
    const navigate = useNavigate();

    const handleSend = async (message: string) => {
        navigate(`/chat?q=${encodeURIComponent(message)}`);
    };

    return (
        <div className="flex h-full justify-center pt-56">
            <div className="w-full max-w-[650px]">
                <h1 className="mb-3 text-4xl font-bold">
                    어떤 청년 지원 정책을 찾고 계신가요?
                </h1>

                <div className="sm mb-10 space-y-2 text-gray-500">
                    <p>
                        지금 청년이 받을 수 있는 지원금은
                        무엇인가요?
                    </p>

                    <p>
                        창업을 하고싶은데 도움을 받을 수 있는
                        정책이 있나요?
                    </p>

                    <p>
                        취업을 해서 집을 알아봐야하는데
                        정부에서 지원해주는 정책이
                        무엇인가요?
                    </p>
                </div>

                <ChatInput onSend={handleSend} />
            </div>
        </div>
    );
};

export default HomePage;
