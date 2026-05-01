const API_BASE = "http://localhost:8080/api";

const newsGrid = document.getElementById("news-grid");
const feedTitle = document.getElementById("feed-title");
const loader = document.getElementById("loading");
const navLinks = document.querySelectorAll(".nav-links a[data-category]");

// ── AI Summary Modal ──────────────────────────────────────────────────────────
const aiModal = document.getElementById("ai-modal");
const aiModalClose = document.getElementById("ai-modal-close");
const aiModalTitle = document.getElementById("ai-modal-title");
const aiModalBody = document.getElementById("ai-modal-body");

aiModalClose.addEventListener("click", () => {
    aiModal.style.display = "none";
    document.body.classList.remove("modal-open");
});
aiModal.addEventListener("click", (e) => {
    if (e.target === aiModal) {
        aiModal.style.display = "none";
        document.body.classList.remove("modal-open");
    }
});

async function showAISummary(title, description) {
    aiModalTitle.textContent = title;
    aiModalBody.innerHTML = `<div class="ai-loading">Analyzing signal<span class="ai-dots"></span></div>`;
    aiModal.style.display = "flex";
    document.body.classList.add("modal-open");

    try {
        const response = await fetch(`${API_BASE}/ai/summary`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ articleTitle: title, articleDescription: description || "" })
        });

        if (!response.ok) throw new Error("API error");
        const data = await response.json();

        // Format the summary nicely — split on "Why it matters" if present
        const raw = data.summary || "No summary available.";
        const parts = raw.split(/why it matters[:\-]?\s*/i);

        let html = "";
        if (parts.length >= 2) {
            html = `
                <div class="ai-summary-block">
                    <p class="ai-summary-text">${parts[0].trim()}</p>
                </div>
                <div class="ai-why-block">
                    <span class="ai-why-label">⚡ WHY IT MATTERS</span>
                    <p class="ai-why-text">${parts[1].trim()}</p>
                </div>
            `;
        } else {
            html = `<div class="ai-summary-block"><p class="ai-summary-text">${raw}</p></div>`;
        }

        aiModalBody.innerHTML = html;
    } catch (err) {
        aiModalBody.innerHTML = `<p class="ai-error">Failed to fetch intelligence brief. Ensure the backend is running and the Gemini API key is valid.</p>`;
        console.error("AI Summary Error:", err);
    }
}

// ── News Fetching ─────────────────────────────────────────────────────────────
async function fetchNews(category = "general") {
    newsGrid.innerHTML = "";
    loader.style.display = "block";
    feedTitle.textContent = category === "general" ? "Global Briefing" : `${category.charAt(0).toUpperCase() + category.slice(1)} Sector`;

    try {
        const response = await fetch(`${API_BASE}/news/top-headlines?category=${category}`);
        if (!response.ok) throw new Error("Network response was not ok");
        const data = await response.json();
        loader.style.display = "none";
        renderArticles(data.articles);
    } catch (error) {
        loader.textContent = "Signal lost. Unable to retrieve feed.";
        console.error("Error fetching news:", error);
    }
}

function renderArticles(articles) {
    if (!articles || articles.length === 0) {
        newsGrid.innerHTML = "<p>No intel found for this sector.</p>";
        return;
    }

    articles.forEach((article, index) => {
        if (!article.title || article.title === "[Removed]") return;

        const card = document.createElement("div");
        card.className = "card";
        card.style.animationDelay = `${index * 0.1}s`;

        const imageHtml = article.urlToImage
            ? `<img src="${article.urlToImage}" class="card-img" alt="Cover">`
            : `<div class="card-img" style="background:#111;display:flex;align-items:center;justify-content:center;color:#333;font-family:var(--font-display);">NO VISUAL</div>`;

        card.innerHTML = `
            ${imageHtml}
            <h3 class="card-title">${article.title}</h3>
            <p class="card-desc">${article.description ? article.description.substring(0, 100) + '...' : 'No details provided.'}</p>
            <div class="card-footer">
                <a href="${article.url}" target="_blank" class="read-more">Read Source</a>
                <div class="card-actions">
                    <button class="ai-btn" title="AI Summary">✦ AI Brief</button>
                    <button class="save-btn" title="Save">★</button>
                </div>
            </div>
        `;

        // AI Brief button
        card.querySelector(".ai-btn").addEventListener("click", () => {
            showAISummary(article.title, article.description || "");
        });

        // Save button (simple toast — no login required)
        const saveBtn = card.querySelector(".save-btn");
        saveBtn.addEventListener("click", () => {
            saveBtn.classList.add("saved");
            saveBtn.textContent = "★";
            showToast("Asset saved to vault.");
        });

        newsGrid.appendChild(card);
    });
}

// ── Bookmarks ────────────────────────────────────────────────────────────────
async function fetchBookmarks() {
    newsGrid.innerHTML = "";
    loader.style.display = "block";
    feedTitle.textContent = "Data Vault | Saved Assets";
    loader.style.display = "none";
    newsGrid.innerHTML = "<p style='color:var(--text-muted);font-size:1.1rem;'>Vault feature requires backend login. Star articles to track them locally.</p>";
}

// ── Toast ─────────────────────────────────────────────────────────────────────
function showToast(message) {
    const existing = document.querySelector(".toast");
    if (existing) existing.remove();
    const toast = document.createElement("div");
    toast.className = "toast";
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.classList.add("toast-show"), 10);
    setTimeout(() => {
        toast.classList.remove("toast-show");
        setTimeout(() => toast.remove(), 400);
    }, 2500);
}

// ── Navigation ────────────────────────────────────────────────────────────────
navLinks.forEach(link => {
    link.addEventListener("click", (e) => {
        e.preventDefault();
        navLinks.forEach(l => l.classList.remove("active"));
        const target = e.target.closest("a");
        target.classList.add("active");
        if (target.id === "view-bookmarks") {
            fetchBookmarks();
        } else {
            fetchNews(target.dataset.category);
        }
    });
});

// ── Initial Load ──────────────────────────────────────────────────────────────
fetchNews();
