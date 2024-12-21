document.getElementById('namepd').readOnly = true;

$(document).ready(function() {
        $('#price').on('input', function() {
            var value = $(this).val().replace(/[^\d.-]/g, '');
            $(this).val(formatCurrency(value));
        });
        
        function formatCurrency(value) {
            return new Intl.NumberFormat('en-US', {style: 'currency'}).format(value);
        }
    });