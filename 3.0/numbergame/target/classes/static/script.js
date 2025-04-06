// script.js

// 定义全局模块
const index = {
    init() {
        this.setupHostButton();
        this.setupPlayerButton();
        this.setupMarkdownButton();
        this.setupReturnButton();
    },

    setupHostButton() {
        document.getElementById("hostButton").addEventListener("click", () => {
            window.location.href = "host.html";
        });
    },

    setupPlayerButton() {
        document.getElementById("playerButton").addEventListener("click", () => {
            window.location.href = "player.html";
        });
    },

    setupMarkdownButton() {
        document.getElementById("markdownButton").addEventListener("click", () => {
            window.location.href = "markdown_displayhtml.html";
        });
    },

    setupReturnButton() {
        document.getElementById("returnButton").addEventListener("click", () => {
            window.location.href = "index.html";
        });
    }
};

const player = {
    init() {
        this.setupNumberForm();
        this.setupReturnButton();
        this.setupMarkdownButton(); // 新增方法
    },

    setupNumberForm() {
        document.getElementById("numberForm").addEventListener("submit", (event) => {
            event.preventDefault();
            const number = parseInt(document.getElementById("number").value);
            if (!number || number < 1 || number > 100) {
                alert("请输入一个介于1到100之间的数字。");
                return;
            }
            const submitButton = document.querySelector('button[type="submit"]');
            submitButton.disabled = true;
            document.getElementById("result").innerHTML = '<p>加载中...</p>';
            fetch("/submit", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ number: number }),
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error("网络响应错误");
                }
                return response.json();
            })
            .then(data => {
                if (data.status === "success") {
                    alert(data.message);
                    if (data.winnerNumber !== undefined) {
                        document.getElementById("result").innerHTML = `
                            <h2>Winner Number：${data.winnerNumber}</h2>
                            <h3>Prize：${data.prize}</h3>
                        `;
                        document.getElementById("result").classList.add("show");
                    }
                } else {
                    alert(data.message);
                }
                document.getElementById("numStudents").innerHTML = `已提交的玩家数量：${data.numStudents}`;
                document.getElementById("participantsDisplay").innerHTML = `参与人数：${data.numParticipants}`;
                document.getElementById("numWinners").innerHTML = `获胜人数：${data.numWinners}`;
            })
            .catch(error => {
                console.error("Error:", error);
                alert("网络错误。请稍后再试。");
            })
            .finally(() => {
                submitButton.disabled = false;
                document.getElementById("number").value = "";
            });
        });
    },

    setupReturnButton() {
        document.getElementById("returnButton").addEventListener("click", () => {
            window.location.href = "index.html";
        });
    },

    setupMarkdownButton() { // 新增方法
        document.getElementById("markdownButton").addEventListener("click", () => {
            window.location.href = "markdown_displayhtml.html";
        });
    }
};

const utils = {
    getStatus() {
        fetch("/status")
            .then(response => response.json())
            .then(data => {
                if (data.status === "success") {
                    document.getElementById("numStudents").innerHTML = `已提交的玩家数量：${data.numStudents}`;
                    document.getElementById("participantsDisplay").innerHTML = `参与人数：${data.numParticipants}`;
                    document.getElementById("numWinners").innerHTML = `获胜人数：${data.numWinners}`;
                    if (data.winnerNumber !== null) {
                        document.getElementById("result").innerHTML = `
                            <h2>Winner Number：${data.winnerNumber}</h2>
                            <h3>Prize：${data.prize}</h3>
                        `;
                        document.getElementById("result").classList.add("show");
                    } else {
                        document.getElementById("result").classList.remove("show");
                    }
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("加载游戏状态失败。请稍后再试。");
            });
    }
};

const host = {
    init() {
        this.setupSetParticipantsForm();
        this.setupResetButton();
        this.setupReturnButton();
        this.setupMarkdownButton(); // 新增方法
    },

    setupSetParticipantsForm() {
        document.getElementById("setParticipantsForm").addEventListener("submit", (event) => {
            event.preventDefault();
            const participants = parseInt(document.getElementById("participants").value);
            if (!participants || participants < 1) {
                alert("请输入一个有效的参与人数。");
                return;
            }
            fetch("/setParticipants", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ number: participants }),
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error("网络响应错误");
                }
                return response.json();
            })
            .then(data => {
                alert(data.message);
                document.getElementById("participantsDisplay").innerHTML = `参与人数：${data.numParticipants}`;
                document.getElementById("numWinners").innerHTML = `获胜人数：${data.numWinners}`;
            })
            .catch(error => {
                console.error("Error:", error);
                alert("网络错误。请稍后再试。");
            });
        });
    },

    setupResetButton() {
        document.getElementById("resetButton").addEventListener("click", () => {
            fetch("/reset", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({}),
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error("网络响应错误");
                }
                return response.json();
            })
            .then(data => {
                if (data.status === "success") {
                    alert(data.message);
                    document.getElementById("result").classList.remove("show");
                    document.getElementById("numStudents").innerHTML = `已提交的玩家数量：0`;
                    document.getElementById("participantsDisplay").innerHTML = `参与人数：${data.numParticipants}`;
                    document.getElementById("numWinners").innerHTML = `获胜人数：0`;
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("网络错误。请稍后再试。");
            });
        });
    },

    setupReturnButton() {
        document.getElementById("returnButton").addEventListener("click", () => {
            window.location.href = "index.html";
        });
    },

    setupMarkdownButton() { // 新增方法
        document.getElementById("markdownButton").addEventListener("click", () => {
            window.location.href = "markdown_displayhtml.html";
        });
    }
};

// 主逻辑
document.addEventListener("DOMContentLoaded", () => {
    const isHostPage = window.location.pathname.endsWith('host.html');
    const isPlayerPage = window.location.pathname.endsWith('player.html');
    const isIndexPage = window.location.pathname.endsWith('index.html');

    if (isHostPage) {
        host.init();
    } else if (isPlayerPage) {
        player.init();
    } else if (isIndexPage) {
        index.init();
    }

    utils.getStatus();
});