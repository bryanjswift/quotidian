(function() {
	var TextAsLabelInput = new Class({
		inputClass: 'defaultText',
		labelClass: 'textAsLabel',
		initialize: function(el) {
			this.el = document.id(el);
			this.label = $$('label[for=' + this.el.get('id') + ']');
			this.defaultText = this.label.get('text');
			this.el.set('value',this.defaultText).addClass(this.inputClass)
						 .addEvents({focus:this.focus.bind(this),blur:this.blur.bind(this)});
			this.label.addClass(this.labelClass);
		},
		blur: function() {
			if (this.el.get('value') == '') { this.el.set('value',this.defaultText).addClass(this.inputClass); }
		},
		focus: function() {
			if (this.el.get('value') == this.defaultText) { this.el.set('value','').removeClass(this.inputClass); }
		}
	});
	var ExpandingTextarea = new Class({
		initialize: function(el) {
			this.el = document.id(el);
		}
	});
	var text = new TextAsLabelInput('text');
	var source = new TextAsLabelInput('source');
	var context = new TextAsLabelInput('context');
})();
