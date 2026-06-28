const Sidebar = () => {
    const items = ["새 채팅", "북마크", "나중에 추가"];
    const policies = Array(4).fill("policy 예시 예시");
    const histories = Array(4).fill("history 예시 예시");

    return (
        <aside className="w-[260px] bg-white px-7 py-5">
            <nav className="space-y-3 sm">
                {items.map((item) => (
                    <button key={item} className="block  hover:font-medium">
                        ㅁ {item}
                    </button>
                ))}
            </nav>

            <section className="mt-14">
                <h3 className="mb-4 sm font-bold">관심정책</h3>
                <div className="space-y-3 sm">
                    {policies.map((item, index) => (
                        <p key={index}>{item}</p>
                    ))}
                </div>
            </section>

            <section className="mt-14">
                <h3 className="mb-4 sm font-bold">이전 대화</h3>
                <div className="space-y-3 sm">
                    {histories.map((item, index) => (
                        <p key={index}>{item}</p>
                    ))}
                </div>
            </section>
        </aside>
    );
};

export default Sidebar;
