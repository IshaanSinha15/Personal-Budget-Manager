(function () {
    function clearSession() {
        localStorage.removeItem("userId");
        localStorage.removeItem("userName");
        localStorage.removeItem("userEmail");
    }

    function initializeDashboardHeader() {
        var userId = localStorage.getItem("userId");
        var userName = localStorage.getItem("userName");

        var userInfo = document.getElementById("userInfo");
        var userNameEl = document.getElementById("userName");
        var userIdEl = document.getElementById("userId");
        var logoutBtn = document.getElementById("logoutBtn");
        var loginLink = document.getElementById("loginLink");
        var registerLink = document.getElementById("registerLink");

        if (userId) {
            if (userInfo) {
                userInfo.style.display = "flex";
            }
            if (userNameEl) {
                userNameEl.textContent = userName || "User";
            }
            if (userIdEl) {
                userIdEl.textContent = "ID: " + userId;
            }
            if (logoutBtn) {
                logoutBtn.style.display = "inline-flex";
            }
            if (loginLink) {
                loginLink.style.display = "none";
            }
            if (registerLink) {
                registerLink.style.display = "none";
            }
        } else {
            if (userInfo) {
                userInfo.style.display = "none";
            }
            if (logoutBtn) {
                logoutBtn.style.display = "none";
            }
            if (loginLink) {
                loginLink.style.display = "inline-flex";
            }
            if (registerLink) {
                registerLink.style.display = "inline-flex";
            }
        }

        if (logoutBtn) {
            logoutBtn.addEventListener("click", function () {
                clearSession();
                window.location.href = "login.html";
            });
        }
    }

    document.addEventListener("DOMContentLoaded", initializeDashboardHeader);
})();
