package com.aiub.esportsclub

// ChatbotEngine is the BRAIN of the chatbot
// It receives the user's message and returns the bot's reply
// This is a rule-based chatbot — it checks keywords and returns fixed answers
object ChatbotEngine {
    // "object" means this is a Singleton — only one instance exists
    // We can call ChatbotEngine.getReply() from anywhere without creating an object

    // ===== MAIN FUNCTION =====
    // Takes the user's message → returns the bot's reply
    fun getReply(userMessage: String): String {

        // Convert to lowercase so matching works regardless of caps
        // "REGISTER" and "register" and "Register" all become "register"
        val msg = userMessage.lowercase().trim()

        // ===== RULE-BASED MATCHING =====
        // We check if the message CONTAINS certain keywords
        // .contains() returns true if the keyword is anywhere in the message

        return when {

            // ── REGISTRATION ──────────────────────────────────────────
            msg.contains("register") ||
                    msg.contains("sign up") ||
                    msg.contains("join") ||
                    msg.contains("enroll") ->
                "📝 To register for a tournament:\n1. Open the sidebar menu ☰\n2. Tap 'Register Now'\n3. Fill in your Name, Student ID and Team Name\n4. Tap Submit!"

            // ── EVENTS ────────────────────────────────────────────────
            msg.contains("event") ||
                    msg.contains("tournament") ||
                    msg.contains("match") ||
                    msg.contains("competition") ->
                "📅 To view upcoming events:\n1. Open the sidebar menu ☰\n2. Tap 'View Events'\nYou will see all upcoming tournaments with dates and prizes!"

            // ── PLAYERS ───────────────────────────────────────────────
            msg.contains("player") ||
                    msg.contains("team") ||
                    msg.contains("member") ||
                    msg.contains("roster") ->
                "👥 To see our players:\n1. Open the sidebar menu ☰\n2. Tap 'Our Players'\nYou will see all club members and their game specialties!"

            // ── PROFILE ───────────────────────────────────────────────
            msg.contains("profile") ||
                    msg.contains("update") ||
                    msg.contains("edit") ||
                    msg.contains("change name") ||
                    msg.contains("change info") ->
                "👤 To update your profile:\n1. Open the sidebar menu ☰\n2. Tap 'My Profile'\n3. Edit your details and tap Save/Update!"

            // ── PASSWORD ──────────────────────────────────────────────
            msg.contains("password") ||
                    msg.contains("forgot") ||
                    msg.contains("reset") ||
                    msg.contains("can't login") ||
                    msg.contains("cannot login") ->
                "🔐 Forgot your password?\n1. Go to the Login screen\n2. Tap 'Forgot Password?'\n3. Enter your email\n4. Check your inbox for a reset link!\n\nAlready logged in? Go to My Profile to change your password."

            // ── CONTACT / ADMIN ───────────────────────────────────────
            msg.contains("contact") ||
                    msg.contains("admin") ||
                    msg.contains("help") ||
                    msg.contains("support") ->
                "📧 Need help from admin?\nEmail us at: admin@aiub.com\nOr visit the AIUB eSports Club page on campus!"

            // ── GAMES ─────────────────────────────────────────────────
            msg.contains("game") ||
                    msg.contains("pubg") ||
                    msg.contains("valorant") ||
                    msg.contains("fifa") ||
                    msg.contains("free fire") ||
                    msg.contains("cs2") ->
                "🎮 We support these games:\n• PUBG Mobile\n• Valorant\n• FIFA\n• Free Fire\n• CS2\n\nCheck 'View Events' for upcoming tournaments for each game!"

            // ── GREETING ──────────────────────────────────────────────
            msg.contains("hi") ||
                    msg.contains("hello") ||
                    msg.contains("hey") ||
                    msg.contains("good morning") ||
                    msg.contains("good afternoon") ||
                    msg.contains("assalamu") ->
                "👋 Hello! I'm the AIUB eSports Club assistant.\n\nI can help you with:\n• 📅 Events & Tournaments\n• 📝 Registration\n• 👥 Players\n• 👤 Profile\n• 🔐 Password help\n\nWhat do you need help with?"

            // ── THANK YOU ─────────────────────────────────────────────
            msg.contains("thank") ||
                    msg.contains("thanks") ||
                    msg.contains("great") ||
                    msg.contains("awesome") ->
                "😊 You're welcome! Is there anything else I can help you with?"

            // ── ABOUT THE CLUB ────────────────────────────────────────
            msg.contains("about") ||
                    msg.contains("what is") ||
                    msg.contains("aiub esports") ||
                    msg.contains("club") ->
                "⚡ AIUB eSports Club is the official gaming club of American International University — Bangladesh!\n\nWe organize tournaments, build teams, and connect gamers across AIUB. Join us and compete!"

            // ── DEFAULT (no keyword matched) ──────────────────────────
            else ->
                "🤔 Sorry, I didn't understand that.\n\nTry asking about:\n• 'How do I register?'\n• 'Show me events'\n• 'Who are the players?'\n• 'How to update profile?'\n• 'I forgot my password'"
        }
    }
}