  document.addEventListener('DOMContentLoaded', function() {
        const colorCards = document.querySelectorAll('.card-color');
        
        colorCards.forEach(card => {
            card.addEventListener('click', function() {
                // Remove 'selected' class from all cards
                colorCards.forEach(c => c.classList.remove('selected'));
                
                // Add 'selected' class to the clicked card
                card.classList.add('selected');
            });
        });
    });
      document.addEventListener('DOMContentLoaded', function() {
        const colorCards = document.querySelectorAll('.card-size');
        
        colorCards.forEach(card => {
            card.addEventListener('click', function() {
                // Remove 'selected' class from all cards
                colorCards.forEach(c => c.classList.remove('selected'));
                
                // Add 'selected' class to the clicked card
                card.classList.add('selected');
            });
        });
    });
    
    function increaseQuantity(button) {
		var input = button.parentNode.querySelector('input[type=number]');
		input.stepUp();
	}

	function decreaseQuantity(button) {
		var input = button.parentNode.querySelector('input[type=number]');
		input.stepDown();
	}
