import type { Page } from "../types/Page";
import { SimpleCard, Spinner } from "@humansignal/ui";
import { IconFolderAdd, IconHumanSignal, IconUserAdd, IconFolderOpen } from "@humansignal/icons";
import { HeidiTips } from "../../components/HeidiTips/HeidiTips";
import { useQuery } from "@tanstack/react-query";
import { useAPI } from "../../providers/ApiProvider";
import { useState } from "react";
import { CreateProject } from "../CreateProject/CreateProject";
import { InviteLink } from "../Organization/PeoplePage/InviteLink";
import { Heading, Sub } from "@humansignal/typography";
import { useHistory } from "react-router";
import { Link } from "react-router-dom";
import { Button } from "../../components";

const PROJECTS_TO_SHOW = 10;

const actions = [
  {
    title: "创建项目",
    icon: IconFolderAdd,
    type: "createProject",
  },
  {
    title: "邀请成员",
    icon: IconUserAdd,
    type: "invitePeople",
  },
] as const;

type Action = (typeof actions)[number]["type"];

export const HomePage: Page = () => {
  const api = useAPI();
  const history = useHistory();
  const [creationDialogOpen, setCreationDialogOpen] = useState(false);
  const [invitationOpen, setInvitationOpen] = useState(false);
  const { data, isFetching, isSuccess, isError } = useQuery({
    queryKey: ["projects", { page_size: 10 }],
    async queryFn() {
      return api.callApi<{ results: APIProject[]; count: number }>("projects", {
        params: { page_size: PROJECTS_TO_SHOW },
      });
    },
  });

  const handleActions = (action: Action) => {
    return () => {
      switch (action) {
        case "createProject":
          setCreationDialogOpen(true);
          break;
        case "invitePeople":
          setInvitationOpen(true);
          break;
      }
    };
  };

  return (
    <main className="p-6 bg-gradient-to-br from-blue-50 to-white">
      <div className="max-w-7xl mx-auto">
        <section className="flex flex-col gap-6">
          <div className="flex flex-col gap-1">
            <Heading size={1} className="text-blue-600">欢迎使用数据标注平台 👋</Heading>
            <Sub className="text-gray-600">开始您的数据标注之旅，让AI更智能</Sub>
          </div>
          <div className="flex justify-start gap-4">
            {actions.map((action) => {
              return (
                <Button
                  key={action.title}
                  rawClassName="flex-grow-0 text-16/24 gap-2 text-white text-left min-w-[250px] [&_svg]:w-6 [&_svg]:h-6 pl-2 bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 rounded-lg shadow-md transition-all duration-300"
                  onClick={handleActions(action.type)}
                >
                  <action.icon className="text-white" />
                  <span>{action.title}</span>
                </Button>
              );
            })}
          </div>

          <SimpleCard
            title={
              data?.count > 0 ? (
                <>
                  我的项目{" "}
                  <a href="/projects" className="text-lg font-normal text-blue-500 hover:text-blue-600 hover:underline">
                    查看全部
                  </a>
                </>
              ) : null
            }
            className="shadow-lg border border-gray-100"
          >
            {isFetching ? (
              <div className="h-64 flex justify-center items-center">
                <Spinner />
              </div>
            ) : isError ? (
              <div className="h-64 flex justify-center items-center text-red-500">无法加载项目</div>
            ) : isSuccess && data?.results.length === 0 ? (
              <div className="flex flex-col justify-center items-center border border-blue-100 bg-blue-50 rounded-lg h-64 p-6">
                <div
                  className={
                    "rounded-full w-16 h-16 flex justify-center items-center bg-gradient-to-r from-blue-500 to-blue-600 text-white shadow-lg"
                  }
                >
                  <IconFolderOpen className="w-8 h-8" />
                </div>
                <Heading size={2} className="mt-4 text-blue-600">创建您的第一个项目</Heading>
                <Sub className="text-gray-600 mt-2">导入数据并设置标注界面开始标注</Sub>
                <Button 
                  primary 
                  rawClassName="mt-6 bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white px-6 py-2 rounded-lg shadow-md transition-all duration-300" 
                  onClick={() => setCreationDialogOpen(true)}
                >
                  <span>创建项目</span>
                </Button>
              </div>
            ) : isSuccess && data?.results.length > 0 ? (
              <div className="flex flex-col gap-2">
                {data.results.map((project) => {
                  return <ProjectSimpleCard key={project.id} project={project} />;
                })}
              </div>
            ) : null}
          </SimpleCard>
        </section>
      </div>
      {creationDialogOpen && <CreateProject onClose={() => setCreationDialogOpen(false)} />}
      <InviteLink opened={invitationOpen} onClosed={() => setInvitationOpen(false)} />
    </main>
  );
};

HomePage.title = "数据标注";
HomePage.path = "/";
HomePage.exact = true;

function ProjectSimpleCard({
  project,
}: {
  project: APIProject;
}) {
  const finished = project.finished_task_number ?? 0;
  const total = project.task_number ?? 0;
  const progress = (total > 0 ? finished / total : 0) * 100;
  const white = "#FFFFFF";
  const color = project.color && project.color !== white ? project.color : "#E1DED5";

  return (
    <Link
      to={`/projects/${project.id}`}
      className="block even:bg-neutral-surface rounded-sm overflow-hidden hover:bg-blue-50 transition-colors duration-200"
      data-external
    >
      <div
        className="grid grid-cols-[minmax(0,1fr)_150px] p-2 py-3 items-center border-l-[3px]"
        style={{ borderLeftColor: color }}
      >
        <div className="flex flex-col gap-1">
          <span className="text-neutral-content">{project.title}</span>
          <div className="text-neutral-content-subtler text-sm">
            已完成: {finished} / {total} 个任务 ({total > 0 ? Math.round((finished / total) * 100) : 0}%)
          </div>
        </div>
        <div className="bg-neutral-surface rounded-full overflow-hidden w-full h-2 shadow-neutral-border-subtle shadow-border-1">
          <div className="bg-positive-surface-hover h-full" style={{ maxWidth: `${progress}%` }} />
        </div>
      </div>
    </Link>
  );
}
