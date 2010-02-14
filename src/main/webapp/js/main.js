(function() {
	var $ = document.id;
	var TextAsLabelInput = new Class({
		inputClass: 'defaultText',
		labelClass: 'textAsLabel',
		initialize: function(el) {
			this.el = el.el ? el.el : $(el);
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
			this.el = el.el ? el.el : $(el);
			this.height = this.el.getSize().y;
			this.clone = (this.el.clone()
					.setStyles({position:'absolute',width:this.el.getStyle('width'),visibility:'hidden',overflow:'hidden'})
					.set({id:this.el.get('id') + 'clone',value:'',disabled:'disabled'}));
			this.el.getParent().adopt(this.clone);
			var update = this.keypress.bind(this);
			this.el.addEvents({keydown:update,keypress:update,keyup:update}).addClass('expandingTextarea').set('tween',{duration:250});
		},
		keypress: function(e) {
			var evt = new Event(e);
			this.clone.set('value',this.el.get('value'));
			this.update();
		},
		update: function() {
			var currentHeight = this.clone.getScrollSize().y;
			if (currentHeight != this.height) {
				this.height = currentHeight;
				this.el.tween('height',currentHeight);
			}
		}
	});
	var text = new TextAsLabelInput(new ExpandingTextarea('text'));
	var source = new TextAsLabelInput('source');
	var context = new TextAsLabelInput('context');
	function redrawElement() {
		var el = $(this);
		el.setStyle('height',el.getSize().y - 1);
		el.removeAttribute.delay(25,el,['style']);
	}
	addEvent('domready',redrawElement.bind('primary'));
})();
