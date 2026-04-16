(function () {
    function setMessage(elementId, text, type) {
        var messageElement = document.getElementById(elementId);
        if (!messageElement) {
            return;
        }

        messageElement.textContent = text;
        messageElement.classList.remove("error", "success");
        if (type) {
            messageElement.classList.add(type);
        }
    }

    async function loginUser(event) {
        if (event) {
            event.preventDefault();
        }

        var emailInput = document.getElementById("email");
        var passwordInput = document.getElementById("password");
        var email = emailInput ? emailInput.value.trim() : "";
        var password = passwordInput ? passwordInput.value : "";

        if (!email || !password) {
            setMessage("loginMessage", "Email and password are required", "error");
            return;
        }

        setMessage("loginMessage", "Logging in...", "");

        try {
            var response = await window.UserApi.login(email, password);
            localStorage.setItem("userId", String(response.id));
            localStorage.setItem("userName", response.name || "");
            localStorage.setItem("userEmail", response.email || "");

            setMessage("loginMessage", response.message || "Login successful", "success");
            window.location.href = "index.html";
        } catch (error) {
            setMessage("loginMessage", error.message || "Login failed", "error");
        }
    }

    async function registerUser(event) {
        if (event) {
            event.preventDefault();
        }

        var nameInput = document.getElementById("name");
        var emailInput = document.getElementById("email");
        var passwordInput = document.getElementById("password");
        var name = nameInput ? nameInput.value.trim() : "";
        var email = emailInput ? emailInput.value.trim() : "";
        var password = passwordInput ? passwordInput.value : "";

        if (!name || !email || !password) {
            setMessage("registerMessage", "Name, email and password are required", "error");
            return;
        }

        setMessage("registerMessage", "Creating your account...", "");

        try {
            var response = await window.UserApi.register(name, email, password);
            setMessage("registerMessage", response.message || "Registration successful. You can now login.", "success");
        } catch (error) {
            setMessage("registerMessage", error.message || "Registration failed", "error");
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        var loginForm = document.getElementById("loginForm");
        if (loginForm) {
            loginForm.addEventListener("submit", loginUser);
        }

        var registerForm = document.getElementById("registerForm");
        if (registerForm) {
            registerForm.addEventListener("submit", registerUser);
        }
    });

    window.loginUser = loginUser;
    window.registerUser = registerUser;
})();