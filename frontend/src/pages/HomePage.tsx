import Header from "./Header";
import Sidebar from "./Sidebar";

const HomePage = () => {

    return (
        <div className="min-h-screen text-gray-900">
            <Header />
            <div className="flex">
                <Sidebar />
            </div>

        </div>
    );
};

export default HomePage;
