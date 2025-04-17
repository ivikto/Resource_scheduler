
function testFunction() {
    const myConst = document.getElementById('hours_container');
    const now = new Date()

    let days = new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate();
    console.log(days)

    const nums = [];
    for (let i = 0; i < 24; i++) {
        nums.push(`<div class="hours">${i}:00</div>`);
    }
    console.log(nums)

    for (let i = 0; i < nums.length; i++) {
        myConst.innerHTML += nums[i]
    }



}

// Инициализация приложения
function initApp() {
    testFunction()
}

// Инициализация при загрузке
document.addEventListener('DOMContentLoaded', function() {
    initApp();
});
