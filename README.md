<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>

<h1>💰 Expense Tracker App</h1>
<p>A personal finance management Android app to <b>track expenses, categorize transactions, set goals, and visualize financial data.</b></p>

<h2>🚀 Features</h2>
<ul>
    <li><b>SQLite Database:</b> Stores transactions, records, categories, goals, parties, and accounts efficiently.</li>
    <li><b>UPI Message Tracking:</b> Uses <code>BroadcastReceiver</code> to detect UPI transactions and auto-assign categories based on <b>amount and party</b>.</li>
    <li><b>Advanced Filtering:</b> View transactions by <b>date, time of day, category, party, and account</b> for better insights.</li>
    <li><b>Goal Management:</b> <code>WorkManager</code> updates goals <b>daily at midnight</b>, ensuring accurate tracking.</li>
    <li><b>Visual Analytics:</b> Integrated <code>MPAndroidChart</code> for graphical insights into expenses.</li>
</ul>

<h2>🛠️ Tech Stack</h2>
<ul>
    <li><b>Languages:</b> Java, XML</li>
    <li><b>Database:</b> SQLite (via <code>SQLiteDatabaseHelper</code>)</li>
    <li><b>Android Components:</b> BroadcastReceiver, WorkManager, RecyclerView, ViewPager2</li>
    <li><b>Libraries:</b> MPAndroidChart</li>
</ul>

<h2>📸 Screenshots</h2>
<p><i>Here are some previews of the Expense Tracker Application</i></p>
<div style="display: flex; gap: 10px; overflow-x: auto;">
    <img src="Screenshots/Home Page.jpg" alt="Home Screen" width="300">
    <img src="Screenshots/Categories Page.jpg" alt="Categories Screen" width="300">
    <img src="Screenshots/Accounts Section.jpg" alt="Accounts Section" width="300">
    <img src="Screenshots/Goals Page.jpg" alt="Goals Screen" width="300">
</div>

<h2>📥 Installation</h2>
<ol>
    <li>Clone the repository:</li>
    <pre><code>git clone https://github.com/AG1713/Expense-Tracker.git</code></pre>
    <li>Open the project in <b>Android Studio</b>.</li>
    <li>Build and run the app on an emulator or a physical device.</li>
</ol>

<h2>🤝 Contributions</h2>
<p>Feel free to fork the repo and submit PRs for improvements! 🚀</p>

</body>
</html>
