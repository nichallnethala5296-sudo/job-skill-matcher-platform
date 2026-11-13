document.addEventListener("DOMContentLoaded", function () {
    const chartCanvas = document.getElementById("skillsDistributionChart");
    if (chartCanvas) {
        fetch("/api/analytics/skills-frequency")
            .then(res => res.json())
            .then(data => {
                new Chart(chartCanvas, {
                    type: "bar",
                    data: {
                        labels: Object.keys(data),
                        datasets: [{
                            label: "Candidate Profiles with Skill",
                            data: Object.values(data),
                            backgroundColor: "#4f46e5"
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: { legend: { display: false } },
                        scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
                    }
                });
            })
            .catch(err => console.error("Error loading analytics:", err));
    }
});