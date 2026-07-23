const API_BASE = "https://opentdb.com";
const STORAGE_KEYS = {
  highScore: "triviaMadnessHighScore",
  bestAccuracy: "triviaMadnessBestAccuracy",
  soloHistory: "triviaMadnessSoloHistory",
  multiplayerHistory: "triviaMadnessMultiplayerHistory"
};

const state = {
  mode: "single",
  players: [],
  settings: {},
  questions: [],
  currentQuestionIndex: 0,
  currentPlayerIndex: 0,
  totalQuestions: 0,
  timerId: null,
  handoffTimerId: null,
  timeRemaining: 15,
  answered: false,
  soundsEnabled: true,
  audioContext: null,
  previousHighScore: 0,
  activeHistoryType: "solo",
  lastSetup: null
};

const screens = [...document.querySelectorAll(".screen")];
const singlePlayerFields = document.getElementById("singlePlayerFields");
const multiplayerFields = document.getElementById("multiplayerFields");
const playerCount = document.getElementById("playerCount");
const playerNameFields = document.getElementById("playerNameFields");
const setupModeLabel = document.getElementById("setupModeLabel");
const playerSectionTitle = document.getElementById("playerSectionTitle");
const questionCountLabel = document.getElementById("questionCountLabel");
const setupError = document.getElementById("setupError");
const categorySelect = document.getElementById("categorySelect");
const gameSetupForm = document.getElementById("gameSetupForm");
const answerGrid = document.getElementById("answerGrid");
const feedbackPanel = document.getElementById("feedbackPanel");
const historyDialog = document.getElementById("historyDialog");
const confirmDialog = document.getElementById("confirmDialog");

function showScreen(id) {
  screens.forEach((screen) => screen.classList.toggle("active", screen.id === id));
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function readHistory(key) {
  try {
    return JSON.parse(localStorage.getItem(key) || "[]");
  } catch {
    return [];
  }
}

function writeHistory(key, values) {
  localStorage.setItem(key, JSON.stringify(values.slice(0, 25)));
}

function loadLocalStats() {
  document.getElementById("savedHighScore").textContent =
    Number(localStorage.getItem(STORAGE_KEYS.highScore) || 0).toLocaleString();
  document.getElementById("multiplayerMatches").textContent =
    readHistory(STORAGE_KEYS.multiplayerHistory).length.toLocaleString();
  document.getElementById("bestAccuracy").textContent =
    `${Number(localStorage.getItem(STORAGE_KEYS.bestAccuracy) || 0)}%`;
}

function createPlayerFields() {
  playerNameFields.innerHTML = "";
  for (let i = 0; i < Number(playerCount.value); i += 1) {
    const label = document.createElement("label");
    label.textContent = `Player ${i + 1} name`;
    const input = document.createElement("input");
    input.type = "text";
    input.maxLength = 20;
    input.value = `Player ${i + 1}`;
    label.appendChild(input);
    playerNameFields.appendChild(label);
  }
}

function setMode(mode) {
  state.mode = mode;
  const single = mode === "single";
  setupModeLabel.textContent = single ? "Solo Player" : "Local Multiplayer";
  playerSectionTitle.textContent = single ? "Player" : "Players";
  questionCountLabel.textContent = single ? "Number of questions" : "Questions per player";
  singlePlayerFields.hidden = !single;
  multiplayerFields.hidden = single;
  setupError.textContent = "";
  if (!single) createPlayerFields();
  showScreen("setupScreen");
}

async function loadCategories() {
  categorySelect.innerHTML = '<option value="">Any category</option>';
  try {
    const response = await fetch(`${API_BASE}/api_category.php`);
    const data = await response.json();
    data.trivia_categories.forEach((category) => {
      const option = document.createElement("option");
      option.value = category.id;
      option.textContent = category.name;
      categorySelect.appendChild(option);
    });
  } catch {
    [[9,"General Knowledge"],[17,"Science & Nature"],[18,"Computers"],[21,"Sports"],[22,"Geography"],[23,"History"]]
      .forEach(([id,name]) => {
        const option = document.createElement("option");
        option.value = id;
        option.textContent = name;
        categorySelect.appendChild(option);
      });
  }
}

function readPlayers() {
  if (state.mode === "single") {
    const name = document.getElementById("singlePlayerName").value.trim() || "Player 1";
    return [{ name, score: 0, correct: 0, answered: 0 }];
  }
  return [...playerNameFields.querySelectorAll("input")].map((input, i) => ({
    name: input.value.trim() || `Player ${i + 1}`,
    score: 0,
    correct: 0,
    answered: 0
  }));
}

function buildApiUrl(amount) {
  const params = new URLSearchParams({ amount: String(amount), type: "multiple" });
  if (state.settings.category) params.set("category", state.settings.category);
  if (state.settings.difficulty) params.set("difficulty", state.settings.difficulty);
  return `${API_BASE}/api.php?${params}`;
}

async function beginGame() {
  await unlockAudio();
  state.players = readPlayers();
  const names = state.players.map((p) => p.name.toLowerCase());
  if (new Set(names).size !== names.length) {
    setupError.textContent = "Each player must use a different name.";
    return;
  }

  state.settings = {
    category: categorySelect.value,
    difficulty: document.getElementById("difficultySelect").value,
    questionsPerPlayer: Number(document.getElementById("questionCountSelect").value),
    timerSeconds: Number(document.getElementById("timerSelect").value)
  };

  state.previousHighScore = Number(localStorage.getItem(STORAGE_KEYS.highScore) || 0);
  state.totalQuestions = state.settings.questionsPerPlayer * state.players.length;
  state.lastSetup = {
    mode: state.mode,
    players: state.players.map((p) => p.name),
    settings: { ...state.settings }
  };

  showScreen("loadingScreen");

  try {
    const response = await fetch(buildApiUrl(state.totalQuestions));
    const data = await response.json();
    if (data.response_code !== 0 || !data.results?.length) throw new Error("Not enough questions were returned.");
    state.questions = data.results.map(normalizeQuestion);
    state.currentQuestionIndex = 0;
    state.currentPlayerIndex = 0;
    state.players.forEach((p) => Object.assign(p, { score: 0, correct: 0, answered: 0 }));

    if (state.mode === "multi") {
      startHandoff();
    } else {
      showScreen("gameScreen");
      renderQuestion();
    }
  } catch (error) {
    showScreen("setupScreen");
    setupError.textContent = `${error.message} Try different settings.`;
  }
}

function normalizeQuestion(q) {
  const correct = decodeHtml(q.correct_answer);
  return {
    category: decodeHtml(q.category),
    difficulty: q.difficulty,
    question: decodeHtml(q.question),
    correctAnswer: correct,
    answers: shuffle([...q.incorrect_answers.map(decodeHtml), correct])
  };
}

function decodeHtml(value) {
  const area = document.createElement("textarea");
  area.innerHTML = value;
  return area.value;
}

function shuffle(items) {
  const array = [...items];
  for (let i = array.length - 1; i > 0; i -= 1) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
  return array;
}

function startHandoff() {
  clearInterval(state.handoffTimerId);
  showScreen("handoffScreen");
  document.getElementById("handoffPlayerName").textContent = state.players[state.currentPlayerIndex].name;
  let count = 3;
  document.getElementById("handoffCountdown").textContent = count;
  playTone(440, 0.08);

  state.handoffTimerId = setInterval(() => {
    count -= 1;
    document.getElementById("handoffCountdown").textContent = count > 0 ? count : "Go!";
    playTone(count > 0 ? 440 : 720, 0.08);

    if (count <= 0) {
      clearInterval(state.handoffTimerId);
      setTimeout(() => {
        showScreen("gameScreen");
        renderQuestion();
      }, 500);
    }
  }, 1000);
}

function renderQuestion() {
  clearInterval(state.timerId);
  state.answered = false;
  feedbackPanel.hidden = true;
  answerGrid.innerHTML = "";

  const q = state.questions[state.currentQuestionIndex];
  const player = state.players[state.currentPlayerIndex];

  document.getElementById("currentPlayerLabel").textContent = state.mode === "single" ? "Player" : "Current player";
  document.getElementById("currentPlayerName").textContent = player.name;
  document.getElementById("questionProgress").textContent = `Question ${state.currentQuestionIndex + 1} of ${state.totalQuestions}`;
  document.getElementById("difficultyBadge").textContent = q.difficulty[0].toUpperCase() + q.difficulty.slice(1);
  document.getElementById("categoryLabel").textContent = q.category;
  document.getElementById("questionText").textContent = q.question;
  const hudMode = document.getElementById("hudMode");
  if (hudMode) hudMode.textContent = state.mode === "single" ? "SOLO MODE" : "MULTIPLAYER MODE";

  renderScoreboard();

  q.answers.forEach((answer) => {
    const button = document.createElement("button");
    button.className = "answer-button";
    button.type = "button";
    button.textContent = answer;
    button.addEventListener("click", () => handleAnswer(button, answer));
    answerGrid.appendChild(button);
  });

  startTimer();
}

function renderScoreboard() {
  const scoreboard = document.getElementById("scoreboard");
  scoreboard.innerHTML = "";
  state.players.forEach((player, index) => {
    const card = document.createElement("article");
    card.className = `player-score${index === state.currentPlayerIndex ? " active" : ""}`;
    card.innerHTML = `<span>${escapeHtml(player.name)}</span><strong>${player.score.toLocaleString()} pts</strong>`;
    scoreboard.appendChild(card);
  });
}

function startTimer() {
  state.timeRemaining = state.settings.timerSeconds;
  updateTimerUi();
  state.timerId = setInterval(() => {
    state.timeRemaining -= 1;
    updateTimerUi();
    if (state.timeRemaining <= 0) {
      clearInterval(state.timerId);
      handleTimeout();
    }
  }, 1000);
}

function updateTimerUi() {
  const percent = Math.max(0, state.timeRemaining / state.settings.timerSeconds * 100);
  document.getElementById("timerText").textContent = state.timeRemaining;
  const bar = document.getElementById("timerBar");
  bar.style.width = `${percent}%`;
  bar.style.background = percent <= 25
    ? "linear-gradient(90deg,#ef5b68,#f6b84a)"
    : "linear-gradient(90deg,var(--secondary),var(--primary))";
}

function handleAnswer(button, answer) {
  if (state.answered) return;
  clearInterval(state.timerId);
  state.answered = true;

  const q = state.questions[state.currentQuestionIndex];
  const player = state.players[state.currentPlayerIndex];
  player.answered += 1;

  if (answer === q.correctAnswer) {
    const bonus = Math.round(state.timeRemaining / state.settings.timerSeconds * 50);
    const points = 100 + bonus;
    player.score += points;
    player.correct += 1;
    button.classList.add("correct");
    showFeedback("Correct!", `+${points} points, including a ${bonus}-point speed bonus.`);
    playTone(660, 0.18);
  } else {
    button.classList.add("incorrect");
    revealCorrect(q.correctAnswer);
    showFeedback("Not quite", `The correct answer was “${q.correctAnswer}.”`);
    playTone(220, 0.22);
  }

  [...answerGrid.children].forEach((b) => b.disabled = true);
  renderScoreboard();
}

function handleTimeout() {
  if (state.answered) return;
  state.answered = true;
  const q = state.questions[state.currentQuestionIndex];
  state.players[state.currentPlayerIndex].answered += 1;
  revealCorrect(q.correctAnswer);
  [...answerGrid.children].forEach((b) => b.disabled = true);
  showFeedback("Time expired", `The correct answer was “${q.correctAnswer}.”`);
  playTone(160, 0.25);
}

function revealCorrect(answer) {
  [...answerGrid.children].forEach((button) => {
    if (button.textContent === answer) button.classList.add("correct");
  });
}

function showFeedback(title, message) {
  document.getElementById("feedbackTitle").textContent = title;
  document.getElementById("feedbackMessage").textContent = message;
  feedbackPanel.hidden = false;
}

function advanceGame() {
  if (state.currentQuestionIndex >= state.totalQuestions - 1) {
    finishGame();
    return;
  }

  state.currentQuestionIndex += 1;

  if (state.mode === "multi") {
    state.currentPlayerIndex = (state.currentPlayerIndex + 1) % state.players.length;
    startHandoff();
  } else {
    renderQuestion();
  }
}

function finishGame() {
  clearInterval(state.timerId);
  saveResults();
  renderResults();
  showScreen("resultsScreen");
}

function saveResults() {
  const totalCorrect = state.players.reduce((sum,p) => sum + p.correct, 0);
  const totalAnswered = state.players.reduce((sum,p) => sum + p.answered, 0);
  const accuracy = totalAnswered ? Math.round(totalCorrect / totalAnswered * 100) : 0;
  const best = Number(localStorage.getItem(STORAGE_KEYS.bestAccuracy) || 0);
  if (accuracy > best) localStorage.setItem(STORAGE_KEYS.bestAccuracy, String(accuracy));

  if (state.mode === "single") {
    const player = state.players[0];
    if (player.score > state.previousHighScore) localStorage.setItem(STORAGE_KEYS.highScore, String(player.score));
    const history = readHistory(STORAGE_KEYS.soloHistory);
    history.unshift({ name: player.name, score: player.score, correct: player.correct, total: player.answered, accuracy, date: new Date().toISOString() });
    writeHistory(STORAGE_KEYS.soloHistory, history);
  } else {
    const sorted = [...state.players].sort((a,b) => b.score - a.score);
    const top = sorted[0].score;
    const winners = sorted.filter((p) => p.score === top).map((p) => p.name);
    const history = readHistory(STORAGE_KEYS.multiplayerHistory);
    history.unshift({
      date: new Date().toISOString(),
      winners,
      topScore: top,
      players: sorted.map((p) => ({
        name: p.name,
        score: p.score,
        correct: p.correct,
        total: p.answered,
        accuracy: p.answered ? Math.round(p.correct / p.answered * 100) : 0
      }))
    });
    writeHistory(STORAGE_KEYS.multiplayerHistory, history);
  }

  loadLocalStats();
}

function renderResults() {
  const winnerBanner = document.getElementById("winnerBanner");
  const soloHeadline = document.getElementById("singlePlayerHeadline");
  const standings = document.getElementById("finalStandings");
  const stats = document.getElementById("singlePlayerResultStats");

  winnerBanner.hidden = true;
  soloHeadline.hidden = true;
  soloHeadline.classList.remove("new-high-score");
  standings.innerHTML = "";
  stats.innerHTML = "";

  const rematchButton = document.getElementById("rematchButton");
  rematchButton.textContent = state.mode === "single" ? "Retry" : "Rematch";

  if (state.mode === "single") {
    const player = state.players[0];
    const accuracy = player.answered ? Math.round(player.correct / player.answered * 100) : 0;
    const newHigh = player.score > state.previousHighScore;
    const saved = Number(localStorage.getItem(STORAGE_KEYS.highScore) || 0);

    document.getElementById("resultsTitle").textContent = newHigh ? "New High Score!" : "Final Score";
    document.getElementById("resultsSubtitle").textContent = newHigh
      ? "Your new personal best was saved on this browser."
      : "Your result was added to your solo score history.";

    soloHeadline.hidden = false;
    soloHeadline.textContent = newHigh
      ? `🎉 ${player.score.toLocaleString()} points — new personal best!`
      : `${player.score.toLocaleString()} points`;
    if (newHigh) soloHeadline.classList.add("new-high-score");

    stats.innerHTML = `
      <article class="result-stat"><span>Final score</span><strong>${player.score.toLocaleString()}</strong></article>
      <article class="result-stat"><span>Accuracy</span><strong>${accuracy}%</strong></article>
      <article class="result-stat"><span>Local high score</span><strong>${saved.toLocaleString()}</strong></article>
    `;
  } else {
    const sorted = [...state.players].sort((a,b) => b.score - a.score);
    const top = sorted[0].score;
    const winners = sorted.filter((p) => p.score === top);

    document.getElementById("resultsTitle").textContent = "Final Standings";
    document.getElementById("resultsSubtitle").textContent = "This match was saved to multiplayer history.";
    winnerBanner.hidden = false;
    winnerBanner.textContent = winners.length === 1
      ? `🏆 ${winners[0].name} wins with ${top.toLocaleString()} points!`
      : `🤝 Tie: ${winners.map((p) => p.name).join(", ")} with ${top.toLocaleString()} points.`;

    sorted.forEach((player,index) => {
      const accuracy = player.answered ? Math.round(player.correct / player.answered * 100) : 0;
      const row = document.createElement("article");
      row.className = "standing-row";
      row.innerHTML = `<strong>#${index + 1}</strong><div><strong>${escapeHtml(player.name)}</strong><div>${player.correct}/${player.answered} correct · ${accuracy}%</div></div><strong>${player.score.toLocaleString()} pts</strong>`;
      standings.appendChild(row);
    });
  }
}

function openHistory(type) {
  state.activeHistoryType = type;
  const list = document.getElementById("historyList");
  list.innerHTML = "";

  if (type === "solo") {
    document.getElementById("historyDialogTitle").textContent = "Solo Score History";
    document.getElementById("historyDialogDescription").textContent = "Saved only on this browser and device.";
    const history = readHistory(STORAGE_KEYS.soloHistory);
    if (!history.length) list.innerHTML = '<div class="empty-history">No solo scores saved yet.</div>';
    history.forEach((entry,index) => {
      const row = document.createElement("article");
      row.className = "history-row";
      row.innerHTML = `<strong>#${index + 1}</strong><div><strong>${escapeHtml(entry.name)}</strong><div>${new Date(entry.date).toLocaleString()} · ${entry.correct}/${entry.total} correct · ${entry.accuracy}%</div></div><strong>${Number(entry.score).toLocaleString()} pts</strong>`;
      list.appendChild(row);
    });
  } else {
    document.getElementById("historyDialogTitle").textContent = "Multiplayer Match History";
    document.getElementById("historyDialogDescription").textContent = "Final standings from local multiplayer matches on this browser.";
    const history = readHistory(STORAGE_KEYS.multiplayerHistory);
    if (!history.length) list.innerHTML = '<div class="empty-history">No multiplayer matches saved yet.</div>';
    history.forEach((match,index) => {
      const row = document.createElement("article");
      row.className = "history-row";
      const standings = match.players.map((p) => `${escapeHtml(p.name)}: ${Number(p.score).toLocaleString()} pts`).join("<br>");
      row.innerHTML = `<strong>#${index + 1}</strong><div><strong>${match.winners.length === 1 ? `Winner: ${escapeHtml(match.winners[0])}` : `Tie: ${match.winners.map(escapeHtml).join(", ")}`}</strong><div>${new Date(match.date).toLocaleString()}<br>${standings}</div></div><strong>${Number(match.topScore).toLocaleString()} pts</strong>`;
      list.appendChild(row);
    });
  }

  if (typeof historyDialog.showModal === "function") historyDialog.showModal();
}

function clearCurrentHistory() {
  localStorage.removeItem(state.activeHistoryType === "solo" ? STORAGE_KEYS.soloHistory : STORAGE_KEYS.multiplayerHistory);
  loadLocalStats();
  openHistory(state.activeHistoryType);
}

function resetAllStats() {
  Object.values(STORAGE_KEYS).forEach((key) => localStorage.removeItem(key));
  loadLocalStats();
  confirmDialog.close();
}

async function unlockAudio() {
  if (!state.soundsEnabled) return;
  try {
    const AudioContext = window.AudioContext || window.webkitAudioContext;
    if (!AudioContext) return;
    if (!state.audioContext) state.audioContext = new AudioContext();
    if (state.audioContext.state === "suspended") await state.audioContext.resume();

    const oscillator = state.audioContext.createOscillator();
    const gain = state.audioContext.createGain();
    gain.gain.value = 0.0001;
    oscillator.connect(gain);
    gain.connect(state.audioContext.destination);
    oscillator.start();
    oscillator.stop(state.audioContext.currentTime + 0.01);
  } catch {}
}

async function playTone(frequency, duration = 0.15) {
  if (!state.soundsEnabled) return;
  await unlockAudio();
  if (!state.audioContext) return;

  const oscillator = state.audioContext.createOscillator();
  const gain = state.audioContext.createGain();

  oscillator.type = "sine";
  oscillator.frequency.value = frequency;
  gain.gain.setValueAtTime(0.06, state.audioContext.currentTime);
  gain.gain.exponentialRampToValueAtTime(0.0001, state.audioContext.currentTime + duration);

  oscillator.connect(gain);
  gain.connect(state.audioContext.destination);
  oscillator.start();
  oscillator.stop(state.audioContext.currentTime + duration);
}

function escapeHtml(value) {
  return String(value).replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll('"',"&quot;").replaceAll("'","&#039;");
}

document.addEventListener("pointerdown", unlockAudio, { once: true });
document.getElementById("singlePlayerButton").addEventListener("click", () => setMode("single"));
document.getElementById("multiplayerButton").addEventListener("click", () => setMode("multi"));
document.getElementById("backToWelcome").addEventListener("click", () => showScreen("welcomeScreen"));
document.getElementById("brandHome").addEventListener("click", () => showScreen("welcomeScreen"));
playerCount.addEventListener("change", createPlayerFields);
gameSetupForm.addEventListener("submit", (event) => { event.preventDefault(); beginGame(); });
document.getElementById("nextQuestionButton").addEventListener("click", advanceGame);
document.getElementById("rematchButton").addEventListener("click", () => {
  if (!state.lastSetup) return showScreen("welcomeScreen");
  state.mode = state.lastSetup.mode;
  state.players = state.lastSetup.players.map((name) => ({ name, score:0, correct:0, answered:0 }));
  state.settings = { ...state.lastSetup.settings };
  state.previousHighScore = Number(localStorage.getItem(STORAGE_KEYS.highScore) || 0);
  state.totalQuestions = state.settings.questionsPerPlayer * state.players.length;
  beginGame();
});
document.getElementById("newGameButton").addEventListener("click", () => showScreen("welcomeScreen"));
document.getElementById("openSoloHistory").addEventListener("click", () => openHistory("solo"));
document.getElementById("openMultiplayerHistory").addEventListener("click", () => openHistory("multi"));
document.getElementById("viewHistoryButton").addEventListener("click", () => openHistory(state.mode === "single" ? "solo" : "multi"));
document.getElementById("closeHistoryButton").addEventListener("click", () => historyDialog.close());
document.getElementById("clearHistoryButton").addEventListener("click", clearCurrentHistory);
document.getElementById("resetScoresButton").addEventListener("click", () => confirmDialog.showModal());
document.getElementById("cancelResetButton").addEventListener("click", () => confirmDialog.close());
document.getElementById("confirmResetButton").addEventListener("click", resetAllStats);
document.getElementById("soundToggle").addEventListener("click", async (event) => {
  state.soundsEnabled = !state.soundsEnabled;
  event.currentTarget.textContent = `Sound: ${state.soundsEnabled ? "On" : "Off"}`;
  event.currentTarget.setAttribute("aria-pressed", String(state.soundsEnabled));
  if (state.soundsEnabled) {
    await unlockAudio();
    playTone(520, 0.12);
  }
});

loadLocalStats();
loadCategories();
createPlayerFields();
