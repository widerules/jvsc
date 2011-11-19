var androidExternal = androidExternal || {};
androidExternal.animations = androidExternal.animations || {};
androidExternal.animations.ICS_IMG_PATH = "/images/ics/";
androidExternal.animations.ics = cheddar.Class(CanvasNode, {
	initialize: function (canvas)
	{
		CanvasNode.initialize.call(this);
		this.canvas = new Canvas(canvas);
		this.canvas.append(this);
		this.particles = [], this.maxD = 2, this.power = 0.1, this.friction = 0.08, this.ratio = 0.1, this.maxD2 = this.maxD * this.maxD, this.a = this.power / this.maxD2, this.mask_path = "M160,279L5,304L1-3l168,1l-4.194,8.354l-1.535,";
		this.mask_path += "8.42L160,279z M283.613,34.506L282,33l-100.00";
		this.mask_path += "2,1.064L180,37v246l100-16L283.613,34.506z M3";
		this.mask_path += "25-1h-39l4.256,13.264L291,258l30-5L325-1z";
		this.mask = new Path(Path.compileSVGPath(this.mask_path), {
			x: 7,
			y: 4,
			clip: true,
			zIndex: 2
		});
		this.append(this.mask);
		var bg_img = new Image;
		bg_img.src = androidExternal.animations.ICS_IMG_PATH + "ics_background.png";
		this.background_layer = new ImageNode(bg_img);
		this.background_layer.setProps(
		{
			zIndex: 1,
			x: 7,
			y: 1
		});
		this.append(this.background_layer);
		var ui_img = new Image;
		ui_img.src = androidExternal.animations.ICS_IMG_PATH + "ics_ui.png";
		this.ui_layer = new ImageNode(ui_img);
		this.ui_layer.setProps(
		{
			zIndex: 3,
			x: 7,
			y: 1
		});
		this.append(this.ui_layer);
		while (this.particles.length < 60) this.addParticle()
	},
	enable: function ()
	{
		this.canvas.play();
		this.addFrameListener(this.update)
	},
	disable: function ()
	{
		this.canvas.stop();
		this.removeFrameListener(this.update)
	},
	addParticle: function ()
	{
		var p = new androidExternal.animations.ics.particle;
		p.x = Math.random() * 350;
		p.y = Math.random() * 315;
		this.particles.push(p);
		this.mask.append(p)
	},
	update: function ()
	{
		var cursorX = this.root.mouseX - 88;
		var cursorY = this.root.mouseY - 77;
		var i = 0,
			l = this.particles.length;
		for (; i < l; i++)
		{
			p = this.particles[i];
			p.y -= p.mvy + p.mn;
			p.x += p.mvx;
			if (p.y < -20)
			{
				p.y = androidExternal.animations.randomFromTo(200, 315);
				p.x = Math.random() * 350;
				p.opacity = 0
			}
			if (p.x > 350) p.x = -50;
			if (p.y > cursorY - 50 && p.y < cursorY + 50 && p.x > cursorX - 50 && p.x < cursorX + 50 && p.isBeam == false)
			{
				var forceX = 0;
				var forceY = 0;
				var disX = cursorX - p.x;
				var disY = cursorY - p.y;
				if (disX > 0) var signX = -1;
				else var signX = 1;
				if (disY > 0) var signY = -1;
				else var signY = 1;
				var dis = disX * disX + disY * disY;
				if (dis < this.maxD2)
				{
					var force = -1 * this.a * dis / 10 * Math.random();
					forceX = disX * disX / dis * signX * force / 10;
					forceY = disY * disY / dis * signY * force / 10
				}
				p.spx = (p.spx * this.friction + -disX * this.ratio + forceX) * 0.5;
				p.spy = (p.spy * this.friction + -disY * this.ratio + forceY) * 0.5
			}
			p.spx *= 0.95;
			p.spy *= 0.9;
			p.x += p.spx;
			p.y += p.spy * 0.8;
			if (p.y < 50) p.opacity = Math.max(0, p.opacity - 0.01);
			else p.opacity = Math.min(0.3, p.opacity + 0.005)
		}
	}
});
androidExternal.animations.ics.particle = cheddar.Class(CanvasNode, {
	initialize: function ()
	{
		this.catchMouse = false;
		this.isBeam = false;
		this.spx = 0;
		this.spy = 0;
		this.mn = 0.1 + Math.random() / 8;
		this.mvx = 0.05 + Math.random() / 4;
		this.mvy = 0.05 + Math.random() / 8;
		this.scale = androidExternal.animations.randomFromTo(1, 10) / 10;
		this.opacity = 0;
		this.color = Math.floor(Math.random() * 5);
		this.compositeOperation = "lighter";
		CanvasNode.initialize.call(this);
		if (this.color == 1)
		{
			this.isBeam = false;
			url = androidExternal.animations.ICS_IMG_PATH + "particleBlue.png"
		}
		else if (this.color == 2)
		{
			this.isBeam = true;
			this.mvx = 0.5;
			this.mvx = 0.1;
			this.scale = androidExternal.animations.randomFromTo(1, 2) / 10;
			url = androidExternal.animations.ICS_IMG_PATH + "beam.png"
		}
		else
		{
			this.isBeam = false;
			url = androidExternal.animations.ICS_IMG_PATH + "particle.png"
		}
		var i = new Image;
		i.src = url;
		var imn = new ImageNode(i);
		this.append(imn)
	}
});
androidExternal.animations.randomFromTo = function (Lower, Upper)
{
	return Math.floor(Math.random() * (Upper - Lower + 1) + Lower)
};
var androidExternal = androidExternal || {};
androidExternal.animations = androidExternal.animations || {};
androidExternal.animations.SDK_IMG_PATH = "/images/sdk/";
androidExternal.animations.sdk = cheddar.Class(CanvasNode, {
	initialize: function (canvas)
	{
		CanvasNode.initialize.call(this);
		this.cubes = [];
		this.canvas = new Canvas(canvas);
		this.canvas.append(this);
		this.canvas.targObj = this;
		this.canvas.addEventListener("mouseover", this.mouseOver);
		this.canvas.addEventListener("mouseout", this.mouseOut);
		this.rollover = false;
		this.mask_path = "M334,116.667l-18.315-0.424l-0.351-18.576l-22.667-2L282,";
		this.mask_path += "97.333v19.333l-40.75-1.042L240.375,117H201V98.25l-46-0";
		this.mask_path += ".125v21l-7.098,0.054l-0.34,1.821h-4.875l-0.344-1.062L1";
		this.mask_path += "17,120v20h-6.25l-0.188-18.812L95.667,121l-0.333-19.333";
		this.mask_path += "L89.167,98H64.833l-0.167-3.583L70,93.958l0.333-15.146l";
		this.mask_path += "-19.5,0.511l0.265,33.406l-5.265-0.898l-1-2.833l-5.667-";
		this.mask_path += "0.666L21,108.833v13.5l-18.167-1.667L1.242,4.953L334,2.";
		this.mask_path += "667V116.667z";
		this.mask = new Path(Path.compileSVGPath(this.mask_path), {
			x: -10,
			y: 42,
			clip: true,
			catchMouse: false
		});
		this.append(this.mask);
		var android_url = androidExternal.animations.SDK_IMG_PATH + "marquee_sdk_android.png";
		this.android = ImageNode.load(android_url);
		this.android.x = 135;
		this.android.y = 101;
		this.android.opacity = 0.72;
		android.catchMouse = false;
		this.append(this.android);
		for (var i = 0; i < 17; i++)
		{
			var cube = new androidExternal.animations.sdk.Cube;
			cube.x = cube.basex = 20 + Math.random() * 280;
			cube.y = cube.basey = 10 + Math.random() * 80;
			this.mask.append(cube);
			this.cubes.push(cube)
		}
	},
	enable: function ()
	{
		this.canvas.play();
		this.addFrameListener(this.update)
	},
	disable: function ()
	{
		this.canvas.stop();
		this.removeFrameListener(this.update)
	},
	update: function ()
	{
		for (var i = 0; i < this.cubes.length; i++)
		{
			if (this.cubes[i].hover)
			{
				this.cubes[i].update();
				if (Math.round(Math.random() * 800) == 1) this.cubes[i].runPath()
			}
			else if (!this.cubes[i].animating) this.cubes[i].hover = true;
			if (this.rollover)
			{
				if (this.android.opacity < 0.9) this.android.opacity += 8.0E-4
			}
			else if (this.android.opacity > 0.75) this.android.opacity -= 8.0E-4
		}
	},
	mouseOver: function ()
	{
		this.targObj.rollover = true
	},
	mouseOut: function ()
	{
		this.targObj.rollover = false
	}
});
androidExternal.animations.sdk.Cube = cheddar.Class(CanvasNode, {
	initialize: function (url)
	{
		CanvasNode.initialize.call(this);
		this.counter = androidExternal.animations.randomFromTo(0, 30), this.hover = true, this.animating = true;
		this.catchMouse = false;
		this.opacity = 0;
		this.hover_inc = Math.random();
		var img_number = Math.round(Math.random() * 12) + 1;
		var url = androidExternal.animations.SDK_IMG_PATH + "cube" + img_number + ".png";
		this.image = ImageNode.load(url);
		this.append(this.image)
	},
	update: function ()
	{
		if (this.hover)
		{
			this.y = this.basey + Math.cos(this.counter / Math.PI / 50) * 12;
			this.counter += this.hover_inc;
			if (this.opacity < 1) this.opacity += 0.02
		}
	},
	runPath: function ()
	{
		var delay = Math.random() * 4;
		var step1time = 3 + Math.random() * 10;
		var step1total = delay + step1time;
		var step2time = 3 + Math.random() * 10;
		var totalFrames = Math.round((step1total + step2time) * 30);
		this.hover = false;
		this.animating = true;
		this.seq = new cheddar.AniSequence(30, totalFrames);
		this.seq.addKeyframe(0, function (t)
		{
			cheddar.tweener.addTween(t, {
				time: step1time,
				delay: delay,
				x: 50 + Math.random() * 200,
				transition: "easeInOutQuad"
			})
		}, [this]);
		this.seq.addKeyframe(Math.round(step1total * 30), function (t)
		{
			cheddar.tweener.addTween(t, {
				time: 3 + Math.random() * 3,
				y: 200,
				transition: "easeInOutQuad"
			})
		}, [this]);
		this.seq.addKeyframe(totalFrames - 1, function (t)
		{
			t.reset()
		}, [this]);
		this.seq.start()
	},
	reset: function ()
	{
		this.hover = true;
		this.animating = false;
		this.x = Math.random() * 300;
		this.y = 25 + Math.random() * 30;
		this.basex = this.x;
		this.basey = this.y;
		this.opacity = 0;
		this.remove(this.image);
		var img_number = Math.round(Math.random() * 12) + 1;
		var url = androidExternal.animations.SDK_IMG_PATH + "cube" + img_number + ".png";
		this.image = ImageNode.load(url);
		this.append(this.image);
		cheddar.tweener.addTween(this, {
			time: 0.5 + Math.random() * 5,
			opacity: 1
		})
	}
});
androidExternal.animations.randomFromTo = function (Lower, Upper)
{
	return Math.floor(Math.random() * (Upper - Lower + 1) + Lower)
};
var androidExternal = androidExternal || {};
androidExternal.animations = androidExternal.animations || {};
androidExternal.animations.APPS_IMG_PATH = "/images/apps/";
androidExternal.animations.apps = cheddar.Class(CanvasNode, {
	initialize: function (canvas)
	{
		CanvasNode.initialize.call(this);
		this.counter = 0;
		this.canvas = new Canvas(canvas);
		this.canvas.append(this);
		this.highlights = [];
		var t_url = androidExternal.animations.APPS_IMG_PATH + "tablet_glow.png";
		var r3_url = androidExternal.animations.APPS_IMG_PATH + "ring3.png";
		var r4_url = androidExternal.animations.APPS_IMG_PATH + "ring4.png";
		this.screenglow = ImageNode.load(t_url);
		this.three_ring_img = ImageNode.load(r3_url);
		this.four_ring_img = ImageNode.load(r4_url);
		this.three_ring = new CanvasNode;
		this.four_ring = new CanvasNode;
		this.three_ring_img.catchMouse = false;
		this.four_ring_img.catchMouse = false;
		this.three_ring.append(this.three_ring_img);
		this.four_ring.append(this.four_ring_img);
		this.screenglow.setProps(
		{
			opacity: 0,
			y: 142,
			x: 14
		});
		this.three_ring.setProps(
		{
			opacity: 0,
			x: 168,
			y: 35
		});
		this.four_ring.setProps(
		{
			opacity: 0,
			x: 23,
			y: 108
		});
		this.append(this.screenglow, this.three_ring, this.four_ring);
		var app_icons = [androidExternal.animations.APPS_IMG_PATH + "icon4-1.png", androidExternal.animations.APPS_IMG_PATH + "icon4-2.png", androidExternal.animations.APPS_IMG_PATH + "icon4-3.png", androidExternal.animations.APPS_IMG_PATH + "icon4-4.png", androidExternal.animations.APPS_IMG_PATH + "icon3-1.png", androidExternal.animations.APPS_IMG_PATH + "icon3-2.png", androidExternal.animations.APPS_IMG_PATH + "icon3-3.png"];
		var icon_info = [
		{
			x: 61,
			y: 39,
			parent: this.four_ring
		}, {
			x: 264,
			y: 82,
			parent: this.four_ring
		}, {
			x: 147,
			y: 177,
			parent: this.four_ring
		}, {
			x: 297,
			y: 197,
			parent: this.four_ring
		}, {
			x: 41,
			y: 92,
			parent: this.three_ring
		}, {
			x: 105,
			y: 30,
			parent: this.three_ring
		}, {
			x: 191,
			y: 69,
			parent: this.three_ring
		}];
		for (var i = 0; i < app_icons.length; i++)
		{
			var ico = ImageNode.load(app_icons[i]);
			ico.x = icon_info[i].x;
			ico.y = icon_info[i].y;
			ico.catchMouse = false;
			var circ = new androidExternal.animations.apps.highlight;
			circ.x = ico.x + 22;
			circ.y = ico.y + 22;
			icon_info[i].parent.append(circ);
			this.highlights.push(circ);
			icon_info[i].parent.append(ico)
		}
	},
	enable: function ()
	{
		this.canvas.play();
		cheddar.tweener.addTween(this.screenglow, {
			time: 2,
			delay: 0.6,
			opacity: 1
		});
		cheddar.tweener.addTween(this.three_ring, {
			time: 2,
			delay: 1,
			y: 20,
			opacity: 1
		});
		cheddar.tweener.addTween(this.four_ring, {
			time: 2,
			delay: 1.2,
			y: 98,
			opacity: 1
		});
		this.addFrameListener(this.update)
	},
	disable: function ()
	{
		this.canvas.stop();
		this.removeFrameListener(this.update)
	},
	update: function ()
	{
		this.three_ring.y = 20 + Math.cos(Math.PI + this.counter) * 8;
		this.four_ring.y = 98 + Math.cos(Math.PI + this.counter + 1) * 8;
		this.counter += 0.02
	}
});
androidExternal.animations.apps.highlight = cheddar.Class(CanvasNode, {
	initialize: function ()
	{
		CanvasNode.initialize.call(this);
		this.animating = false;
		this.highlight = new Gradient(
		{
			type: "radial",
			startX: 0,
			startY: 0,
			endRadius: 30,
			colorStops: [
				[1, "rgba(255,255,255,0)"],
				[0.4, "rgba(255,255,255,.3)"],
				[0.9, "rgba(255,255,255,0)"]
			]
		});
		this.circ = new Circle(30, {
			fill: this.highlight,
			compositeOperation: "lighter",
			opacity: 0
		});
		var t = this;
		this.circ.addEventListener("mouseover", function ()
		{
			t.mouseOver()
		});
		this.circ.addEventListener("mouseout", function ()
		{
			t.mouseOut()
		});
		this.append(this.circ)
	},
	mouseOver: function ()
	{
		cheddar.tweener.removeTweensOf(this.circ);
		cheddar.tweener.addTween(this.circ, {
			time: 0.25,
			scale: 3,
			opacity: 1,
			transition: "easeInOutQuad"
		})
	},
	mouseOut: function ()
	{
		cheddar.tweener.removeTweensOf(this.circ);
		cheddar.tweener.addTween(this.circ, {
			time: 0.5,
			scale: 1,
			opacity: 0,
			transition: "easeInOutQuad"
		})
	}
});

(function ()
{
	function g(a)
	{
		throw a;
	}
	var i = void 0,
		k = true,
		l = null,
		n = false;

	function o(a)
	{
		return function ()
		{
			return this[a]
		}
	}
	var q, r = this;

	function aa(a, b)
	{
		var c = a.split("."),
			d = r;
		!(c[0] in d) && d.execScript && d.execScript("var " + c[0]);
		for (var e; c.length && (e = c.shift());)!c.length && b !== i ? d[e] = b : d = d[e] ? d[e] : d[e] = {}
	}
	function ba(a)
	{
		for (var a = a.split("."), b = r, c; c = a.shift();) if (b[c] != l) b = b[c];
		else return l;
		return b
	}
	function t()
	{}

	function ca(a)
	{
		var b = typeof a;
		if (b == "object") if (a)
		{
			if (a instanceof Array) return "array";
			else if (a instanceof Object) return b;
			var c = Object.prototype.toString.call(a);
			if (c == "[object Window]") return "object";
			if (c == "[object Array]" || typeof a.length == "number" && typeof a.splice != "undefined" && typeof a.propertyIsEnumerable != "undefined" && !a.propertyIsEnumerable("splice")) return "array";
			if (c == "[object Function]" || typeof a.call != "undefined" && typeof a.propertyIsEnumerable != "undefined" && !a.propertyIsEnumerable("call")) return "function"
		}
		else return "null";
		else if (b == "function" && typeof a.call == "undefined") return "object";
		return b
	}
	function u(a)
	{
		return ca(a) == "array"
	}
	function da(a)
	{
		var b = ca(a);
		return b == "array" || b == "object" && typeof a.length == "number"
	}
	function v(a)
	{
		return typeof a == "string"
	}
	function w(a)
	{
		return ca(a) == "function"
	}
	function ea(a)
	{
		a = ca(a);
		return a == "object" || a == "array" || a == "function"
	}
	function x(a)
	{
		return a[fa] || (a[fa] = ++ga)
	}
	var fa = "closure_uid_" + Math.floor(Math.random() * 2147483648).toString(36),
		ga = 0;

	function ha(a, b, c)
	{
		return a.call.apply(a.bind, arguments)
	}
	function ia(a, b, c)
	{
		a || g(Error());
		if (arguments.length > 2)
		{
			var d = Array.prototype.slice.call(arguments, 2);
			return function ()
			{
				var c = Array.prototype.slice.call(arguments);
				Array.prototype.unshift.apply(c, d);
				return a.apply(b, c)
			}
		}
		else return function ()
		{
			return a.apply(b, arguments)
		}
	}
	function y(a, b, c)
	{
		y = Function.prototype.bind && Function.prototype.bind.toString().indexOf("native code") != -1 ? ha : ia;
		return y.apply(l, arguments)
	}

	function ja(a, b)
	{
		var c = Array.prototype.slice.call(arguments, 1);
		return function ()
		{
			var b = Array.prototype.slice.call(arguments);
			b.unshift.apply(b, c);
			return a.apply(this, b)
		}
	}
	var z = Date.now ||
	function ()
	{
		return +new Date
	};

	function C(a, b)
	{
		function c()
		{}
		c.prototype = b.prototype;
		a.c = b.prototype;
		a.prototype = new c
	}
	Function.prototype.bind = Function.prototype.bind ||
	function (a, b)
	{
		if (arguments.length > 1)
		{
			var c = Array.prototype.slice.call(arguments, 1);
			c.unshift(this, a);
			return y.apply(l, c)
		}
		else return y(this, a)
	};

	function E()
	{}
	E.prototype.Ma = n;
	E.prototype.h = function ()
	{
		if (!this.Ma) this.Ma = k, this.a()
	};
	E.prototype.a = function ()
	{
		this.lb && ka.apply(l, this.lb)
	};

	function ka(a)
	{
		for (var b = 0, c = arguments.length; b < c; ++b)
		{
			var d = arguments[b];
			da(d) ? ka.apply(l, d) : d && typeof d.h == "function" && d.h()
		}
	};

	function la(a)
	{
		this.stack = Error().stack || "";
		if (a) this.message = String(a)
	}
	C(la, Error);
	la.prototype.name = "CustomError";

	function ma(a, b)
	{
		for (var c = 1; c < arguments.length; c++) var d = String(arguments[c]).replace(/\$/g, "$$$$"),
			a = a.replace(/\%s/, d);
		return a
	}
	var na = /^[a-zA-Z0-9\-_.!~*'()]*$/;

	function oa(a)
	{
		a = String(a);
		return !na.test(a) ? encodeURIComponent(a) : a
	}
	function F(a)
	{
		if (!pa.test(a)) return a;
		a.indexOf("&") != -1 && (a = a.replace(qa, "&amp;"));
		a.indexOf("<") != -1 && (a = a.replace(ra, "&lt;"));
		a.indexOf(">") != -1 && (a = a.replace(sa, "&gt;"));
		a.indexOf('"') != -1 && (a = a.replace(ta, "&quot;"));
		return a
	}
	var qa = /&/g,
		ra = /</g,
		sa = />/g,
		ta = /\"/g,
		pa = /[&<>\"]/;

	function ua(a, b)
	{
		for (var c = 0, d = String(a).replace(/^[\s\xa0]+|[\s\xa0]+$/g, "").split("."), e = String(b).replace(/^[\s\xa0]+|[\s\xa0]+$/g, "").split("."), f = Math.max(d.length, e.length), h = 0; c == 0 && h < f; h++)
		{
			var j = d[h] || "",
				m = e[h] || "",
				p = RegExp("(\\d*)(\\D*)", "g"),
				D = RegExp("(\\d*)(\\D*)", "g");
			do
			{
				var s = p.exec(j) || ["", "", ""],
					A = D.exec(m) || ["", "", ""];
				if (s[0].length == 0 && A[0].length == 0) break;
				c = va(s[1].length == 0 ? 0 : parseInt(s[1], 10), A[1].length == 0 ? 0 : parseInt(A[1], 10)) || va(s[2].length == 0, A[2].length == 0) || va(s[2], A[2])
			} while (c == 0)
		}
		return c
	}
	function va(a, b)
	{
		if (a < b) return -1;
		else if (a > b) return 1;
		return 0
	}
	var wa = {};

	function xa(a)
	{
		return wa[a] || (wa[a] = String(a).replace(/([A-Z])/g, "-$1").toLowerCase())
	};

	function ya(a, b)
	{
		b.unshift(a);
		la.call(this, ma.apply(l, b));
		b.shift();
		this.Uc = a
	}
	C(ya, la);
	ya.prototype.name = "AssertionError";

	function za(a, b)
	{
		g(new ya("Failure" + (a ? ": " + a : ""), Array.prototype.slice.call(arguments, 1)))
	};
	var G = Array.prototype,
		Aa = G.indexOf ?
	function (a, b, c)
	{
		return G.indexOf.call(a, b, c)
	} : function (a, b, c)
	{
		c = c == l ? 0 : c < 0 ? Math.max(0, a.length + c) : c;
		if (v(a)) return !v(b) || b.length != 1 ? -1 : a.indexOf(b, c);
		for (; c < a.length; c++) if (c in a && a[c] === b) return c;
		return -1
	}, Ba = G.forEach ?
	function (a, b, c)
	{
		G.forEach.call(a, b, c)
	} : function (a, b, c)
	{
		for (var d = a.length, e = v(a) ? a.split("") : a, f = 0; f < d; f++) f in e && b.call(c, e[f], f, a)
	}, Ca = G.filter ?
	function (a, b, c)
	{
		return G.filter.call(a, b, c)
	} : function (a, b, c)
	{
		for (var d = a.length, e = [], f = 0, h = v(a) ? a.split("") : a, j = 0; j < d; j++) if (j in h)
		{
			var m = h[j];
			b.call(c, m, j, a) && (e[f++] = m)
		}
		return e
	};

	function Da(a)
	{
		return G.concat.apply(G, arguments)
	}
	function Ea(a)
	{
		if (u(a)) return Da(a);
		else
		{
			for (var b = [], c = 0, d = a.length; c < d; c++) b[c] = a[c];
			return b
		}
	}
	function Fa(a, b, c, d)
	{
		G.splice.apply(a, Ga(arguments, 1))
	}
	function Ga(a, b, c)
	{
		return arguments.length <= 2 ? G.slice.call(a, b) : G.slice.call(a, b, c)
	};
	var Ha, Ia, Ja, Ka, La;

	function Ma()
	{
		return r.navigator ? r.navigator.userAgent : l
	}
	function Na()
	{
		return r.navigator
	}
	Ka = Ja = Ia = Ha = n;
	var Oa;
	if (Oa = Ma())
	{
		var Pa = Na();
		Ha = Oa.indexOf("Opera") == 0;
		Ia = !Ha && Oa.indexOf("MSIE") != -1;
		Ja = !Ha && Oa.indexOf("WebKit") != -1;
		Ka = !Ha && !Ja && Pa.product == "Gecko"
	}
	var Qa = Ha,
		H = Ia,
		Ra = Ka,
		Sa = Ja,
		Ta = Na();
	La = (Ta && Ta.platform || "").indexOf("Mac") != -1;
	var Ua = !! Na() && (Na().appVersion || "").indexOf("X11") != -1,
		Va;
	a: {
		var Wa = "",
			Xa;
		if (Qa && r.opera) var Ya = r.opera.version,
			Wa = typeof Ya == "function" ? Ya() : Ya;
		else if (Ra ? Xa = /rv\:([^\);]+)(\)|;)/ : H ? Xa = /MSIE\s+([^\);]+)(\)|;)/ : Sa && (Xa = /WebKit\/(\S+)/), Xa) var Za = Xa.exec(Ma()),
			Wa = Za ? Za[1] : "";
		if (H)
		{
			var $a, ab = r.document;
			$a = ab ? ab.documentMode : i;
			if ($a > parseFloat(Wa))
			{
				Va = String($a);
				break a
			}
		}
		Va = Wa
	}
	var bb = {};

	function cb(a)
	{
		return bb[a] || (bb[a] = ua(Va, a) >= 0)
	}
	var db = {};

	function eb()
	{
		return db[9] || (db[9] = H && document.documentMode && document.documentMode >= 9)
	};
	var fb;
	!H || eb();
	H && cb("8");

	function I(a, b)
	{
		this.type = a;
		this.currentTarget = this.target = b
	}
	C(I, E);
	I.prototype.a = function ()
	{
		delete this.type;
		delete this.target;
		delete this.currentTarget
	};
	I.prototype.B = n;
	I.prototype.fa = k;
	I.prototype.stopPropagation = function ()
	{
		this.B = k
	};

	function gb(a)
	{
		a.stopPropagation()
	};
	var hb = {
		Wb: "click",
		ac: "dblclick",
		uc: "mousedown",
		yc: "mouseup",
		xc: "mouseover",
		wc: "mouseout",
		vc: "mousemove",
		Ic: "selectstart",
		pc: "keypress",
		oc: "keydown",
		qc: "keyup",
		Ub: "blur",
		ic: "focus",
		bc: "deactivate",
		jc: H ? "focusin" : "DOMFocusIn",
		kc: H ? "focusout" : "DOMFocusOut",
		Vb: "change",
		Hc: "select",
		Jc: "submit",
		nc: "input",
		Dc: "propertychange",
		fc: "dragstart",
		cc: "dragenter",
		ec: "dragover",
		dc: "dragleave",
		gc: "drop",
		Nc: "touchstart",
		Mc: "touchmove",
		Lc: "touchend",
		Kc: "touchcancel",
		Yb: "contextmenu",
		hc: "error",
		mc: "help",
		rc: "load",
		sc: "losecapture",
		Ec: "readystatechange",
		Fc: "resize",
		Gc: "scroll",
		Oc: "unload",
		lc: "hashchange",
		zc: "pagehide",
		Ac: "pageshow",
		Cc: "popstate",
		Zb: "copy",
		Bc: "paste",
		$b: "cut",
		Rb: "beforecopy",
		Sb: "beforecut",
		Tb: "beforepaste",
		tc: "message",
		Xb: "connect"
	};

	function ib(a)
	{
		ib[" "](a);
		return a
	}
	ib[" "] = t;

	function jb(a, b)
	{
		a && this.Y(a, b)
	}
	C(jb, I);
	q = jb.prototype;
	q.target = l;
	q.relatedTarget = l;
	q.offsetX = 0;
	q.offsetY = 0;
	q.clientX = 0;
	q.clientY = 0;
	q.screenX = 0;
	q.screenY = 0;
	q.button = 0;
	q.keyCode = 0;
	q.charCode = 0;
	q.ctrlKey = n;
	q.altKey = n;
	q.shiftKey = n;
	q.metaKey = n;
	q.Fb = n;
	q.u = l;
	q.Y = function (a, b)
	{
		var c = this.type = a.type;
		I.call(this, c);
		this.target = a.target || a.srcElement;
		this.currentTarget = b;
		var d = a.relatedTarget;
		if (d)
		{
			if (Ra)
			{
				var e;
				a: {
					try
					{
						ib(d.nodeName);
						e = k;
						break a
					}
					catch (f)
					{}
					e = n
				}
				e || (d = l)
			}
		}
		else if (c == "mouseover") d = a.fromElement;
		else if (c == "mouseout") d = a.toElement;
		this.relatedTarget = d;
		this.offsetX = a.offsetX !== i ? a.offsetX : a.layerX;
		this.offsetY = a.offsetY !== i ? a.offsetY : a.layerY;
		this.clientX = a.clientX !== i ? a.clientX : a.pageX;
		this.clientY = a.clientY !== i ? a.clientY : a.pageY;
		this.screenX = a.screenX || 0;
		this.screenY = a.screenY || 0;
		this.button = a.button;
		this.keyCode = a.keyCode || 0;
		this.charCode = a.charCode || (c == "keypress" ? a.keyCode : 0);
		this.ctrlKey = a.ctrlKey;
		this.altKey = a.altKey;
		this.shiftKey = a.shiftKey;
		this.metaKey = a.metaKey;
		this.Fb = La ? a.metaKey : a.ctrlKey;
		this.state = a.state;
		this.u = a;
		delete this.fa;
		delete this.B
	};
	q.stopPropagation = function ()
	{
		jb.c.stopPropagation.call(this);
		this.u.stopPropagation ? this.u.stopPropagation() : this.u.cancelBubble = k
	};
	q.ob = o("u");
	q.a = function ()
	{
		jb.c.a.call(this);
		this.relatedTarget = this.currentTarget = this.target = this.u = l
	};

	function kb()
	{}
	var lb = 0;
	q = kb.prototype;
	q.key = 0;
	q.I = n;
	q.Ia = n;
	q.Y = function (a, b, c, d, e, f)
	{
		w(a) ? this.Xa = k : a && a.handleEvent && w(a.handleEvent) ? this.Xa = n : g(Error("Invalid listener argument"));
		this.M = a;
		this.cb = b;
		this.src = c;
		this.type = d;
		this.capture = !! e;
		this.sa = f;
		this.Ia = n;
		this.key = ++lb;
		this.I = n
	};
	q.handleEvent = function (a)
	{
		return this.Xa ? this.M.call(this.sa || this.src, a) : this.M.handleEvent.call(this.M, a)
	};

	function J(a, b)
	{
		this.Za = b;
		this.z = [];
		a > this.Za && g(Error("[goog.structs.SimplePool] Initial cannot be greater than max"));
		for (var c = 0; c < a; c++) this.z.push(this.o ? this.o() : {})
	}
	C(J, E);
	J.prototype.o = l;
	J.prototype.La = l;

	function mb(a)
	{
		return a.z.length ? a.z.pop() : a.o ? a.o() : {}
	}
	function nb(a, b)
	{
		a.z.length < a.Za ? a.z.push(b) : ob(a, b)
	}
	function ob(a, b)
	{
		if (a.La) a.La(b);
		else if (ea(b)) if (w(b.h)) b.h();
		else for (var c in b) delete b[c]
	}
	J.prototype.a = function ()
	{
		J.c.a.call(this);
		for (var a = this.z; a.length;) ob(this, a.pop());
		delete this.z
	};
	var pb, qb = (pb = "ScriptEngine" in r && r.ScriptEngine() == "JScript") ? r.ScriptEngineMajorVersion() + "." + r.ScriptEngineMinorVersion() + "." + r.ScriptEngineBuildVersion() : "0";
	var rb, sb, tb, ub, vb, wb, xb, yb, zb, Ab, Bb;
	(function ()
	{
		function a()
		{
			return {
				j: 0,
				m: 0
			}
		}
		function b()
		{
			return []
		}
		function c()
		{
			function a(b)
			{
				b = h.call(a.src, a.key, b);
				if (!b) return b
			}
			return a
		}
		function d()
		{
			return new kb
		}
		function e()
		{
			return new jb
		}
		var f = pb && !(ua(qb, "5.7") >= 0),
			h;
		wb = function (a)
		{
			h = a
		};
		if (f)
		{
			rb = function ()
			{
				return mb(j)
			};
			sb = function (a)
			{
				nb(j, a)
			};
			tb = function ()
			{
				return mb(m)
			};
			ub = function (a)
			{
				nb(m, a)
			};
			vb = function ()
			{
				return mb(p)
			};
			xb = function ()
			{
				nb(p, c())
			};
			yb = function ()
			{
				return mb(D)
			};
			zb = function (a)
			{
				nb(D, a)
			};
			Ab = function ()
			{
				return mb(s)
			};
			Bb = function (a)
			{
				nb(s, a)
			};
			var j = new J(0, 600);
			j.o = a;
			var m = new J(0, 600);
			m.o = b;
			var p = new J(0, 600);
			p.o = c;
			var D = new J(0, 600);
			D.o = d;
			var s = new J(0, 600);
			s.o = e
		}
		else rb = a, sb = t, tb = b, ub = t, vb = c, xb = t, yb = d, zb = t, Ab = e, Bb = t
	})();

	function Cb(a, b)
	{
		for (var c in a) b.call(i, a[c], c, a)
	}
	function Db(a)
	{
		var b = [],
			c = 0,
			d;
		for (d in a) b[c++] = a[d];
		return b
	}
	function Eb()
	{
		var a = Fb,
			b;
		for (b in a) return n;
		return k
	}
	var Gb = "constructor,hasOwnProperty,isPrototypeOf,propertyIsEnumerable,toLocaleString,toString,valueOf".split(",");

	function Hb(a, b)
	{
		for (var c, d, e = 1; e < arguments.length; e++)
		{
			d = arguments[e];
			for (c in d) a[c] = d[c];
			for (var f = 0; f < Gb.length; f++) c = Gb[f], Object.prototype.hasOwnProperty.call(d, c) && (a[c] = d[c])
		}
	};
	var Ib = {},
		K = {},
		L = {},
		Jb = {};

	function M(a, b, c, d, e)
	{
		if (b) if (u(b))
		{
			for (var f = 0; f < b.length; f++) M(a, b[f], c, d, e);
			return l
		}
		else
		{
			var d = !! d,
				h = K;
			b in h || (h[b] = rb());
			h = h[b];
			d in h || (h[d] = rb(), h.j++);
			var h = h[d],
				j = x(a),
				m;
			h.m++;
			if (h[j])
			{
				m = h[j];
				for (f = 0; f < m.length; f++) if (h = m[f], h.M == c && h.sa == e)
				{
					if (h.I) break;
					return m[f].key
				}
			}
			else m = h[j] = tb(), h.j++;
			f = vb();
			f.src = a;
			h = yb();
			h.Y(c, f, a, b, d, e);
			c = h.key;
			f.key = c;
			m.push(h);
			Ib[c] = h;
			L[j] || (L[j] = tb());
			L[j].push(h);
			a.addEventListener ? (a == r || !a.Ka) && a.addEventListener(b, f, d) : a.attachEvent(b in Jb ? Jb[b] : Jb[b] = "on" + b, f);
			return c
		}
		else g(Error("Invalid event type"))
	}
	function Kb(a, b, c, d, e)
	{
		if (u(b)) for (var f = 0; f < b.length; f++) Kb(a, b[f], c, d, e);
		else
		{
			d = !! d;
			a: {
				f = K;
				if (b in f && (f = f[b], d in f && (f = f[d], a = x(a), f[a])))
				{
					a = f[a];
					break a
				}
				a = l
			}
			if (a) for (f = 0; f < a.length; f++) if (a[f].M == c && a[f].capture == d && a[f].sa == e)
			{
				Lb(a[f].key);
				break
			}
		}
	}

	function Lb(a)
	{
		if (!Ib[a]) return n;
		var b = Ib[a];
		if (b.I) return n;
		var c = b.src,
			d = b.type,
			e = b.cb,
			f = b.capture;
		c.removeEventListener ? (c == r || !c.Ka) && c.removeEventListener(d, e, f) : c.detachEvent && c.detachEvent(d in Jb ? Jb[d] : Jb[d] = "on" + d, e);
		c = x(c);
		e = K[d][f][c];
		if (L[c])
		{
			var h = L[c],
				j = Aa(h, b);
			j >= 0 && G.splice.call(h, j, 1);
			h.length == 0 && delete L[c]
		}
		b.I = k;
		e.$a = k;
		Mb(d, f, c, e);
		delete Ib[a];
		return k
	}

	function Mb(a, b, c, d)
	{
		if (!d.$ && d.$a)
		{
			for (var e = 0, f = 0; e < d.length; e++) if (d[e].I)
			{
				var h = d[e].cb;
				h.src = l;
				xb(h);
				zb(d[e])
			}
			else e != f && (d[f] = d[e]), f++;
			d.length = f;
			d.$a = n;
			f == 0 && (ub(d), delete K[a][b][c], K[a][b].j--, K[a][b].j == 0 && (sb(K[a][b]), delete K[a][b], K[a].j--), K[a].j == 0 && (sb(K[a]), delete K[a]))
		}
	}

	function Nb(a)
	{
		var b, c = 0,
			d = b == l;
		b = !! b;
		if (a == l) Cb(L, function (a)
		{
			for (var e = a.length - 1; e >= 0; e--)
			{
				var f = a[e];
				if (d || b == f.capture) Lb(f.key), c++
			}
		});
		else if (a = x(a), L[a]) for (var a = L[a], e = a.length - 1; e >= 0; e--)
		{
			var f = a[e];
			if (d || b == f.capture) Lb(f.key), c++
		}
	}
	function Ob(a, b, c, d, e)
	{
		var f = 1,
			b = x(b);
		if (a[b])
		{
			a.m--;
			a = a[b];
			a.$ ? a.$++ : a.$ = 1;
			try
			{
				for (var h = a.length, j = 0; j < h; j++)
				{
					var m = a[j];
					m && !m.I && (f &= Pb(m, e) !== n)
				}
			}
			finally
			{
				a.$--, Mb(c, d, b, a)
			}
		}
		return Boolean(f)
	}

	function Pb(a, b)
	{
		var c = a.handleEvent(b);
		a.Ia && Lb(a.key);
		return c
	}
	wb(function (a, b)
	{
		if (!Ib[a]) return k;
		var c = Ib[a],
			d = c.type,
			e = K;
		if (!(d in e)) return k;
		var e = e[d],
			f, h;
		fb === i && (fb = H && !r.addEventListener);
		if (fb)
		{
			f = b || ba("window.event");
			var j = k in e,
				m = n in e;
			if (j)
			{
				if (f.keyCode < 0 || f.returnValue != i) return k;
				a: {
					var p = n;
					if (f.keyCode == 0) try
					{
						f.keyCode = -1;
						break a
					}
					catch (D)
					{
						p = k
					}
					if (p || f.returnValue == i) f.returnValue = k
				}
			}
			p = Ab();
			p.Y(f, this);
			f = k;
			try
			{
				if (j)
				{
					for (var s = tb(), A = p.currentTarget; A; A = A.parentNode) s.push(A);
					h = e[k];
					h.m = h.j;
					for (var B = s.length - 1; !p.B && B >= 0 && h.m; B--) p.currentTarget = s[B], f &= Ob(h, s[B], d, k, p);
					if (m)
					{
						h = e[n];
						h.m = h.j;
						for (B = 0; !p.B && B < s.length && h.m; B++) p.currentTarget = s[B], f &= Ob(h, s[B], d, n, p)
					}
				}
				else f = Pb(c, p)
			}
			finally
			{
				if (s) s.length = 0, ub(s);
				p.h();
				Bb(p)
			}
			return f
		}
		d = new jb(b, this);
		try
		{
			f = Pb(c, d)
		}
		finally
		{
			d.h()
		}
		return f
	});
	var Qb = 0;

	function N(a)
	{
		return a + "_" + Qb++
	};

	function O()
	{}
	C(O, E);
	q = O.prototype;
	q.Ka = k;
	q.ca = l;
	q.ja = function (a)
	{
		this.ca = a
	};
	q.addEventListener = function (a, b, c, d)
	{
		M(this, a, b, c, d)
	};
	q.removeEventListener = function (a, b, c, d)
	{
		Kb(this, a, b, c, d)
	};
	q.dispatchEvent = function (a)
	{
		var b = a.type || a,
			c = K;
		if (b in c)
		{
			if (v(a)) a = new I(a, this);
			else if (a instanceof I) a.target = a.target || this;
			else
			{
				var d = a,
					a = new I(b, this);
				Hb(a, d)
			}
			var d = 1,
				e, c = c[b],
				b = k in c,
				f;
			if (b)
			{
				e = [];
				for (f = this; f; f = f.ca) e.push(f);
				f = c[k];
				f.m = f.j;
				for (var h = e.length - 1; !a.B && h >= 0 && f.m; h--) a.currentTarget = e[h], d &= Ob(f, e[h], a.type, k, a) && a.fa != n
			}
			if (n in c) if (f = c[n], f.m = f.j, b) for (h = 0; !a.B && h < e.length && f.m; h++) a.currentTarget = e[h], d &= Ob(f, e[h], a.type, n, a) && a.fa != n;
			else for (e = this; !a.B && e && f.m; e = e.ca) a.currentTarget = e, d &= Ob(f, e, a.type, n, a) && a.fa != n;
			a = Boolean(d)
		}
		else a = k;
		return a
	};
	q.a = function ()
	{
		O.c.a.call(this);
		Nb(this);
		this.ca = l
	};

	function Rb(a, b)
	{
		this.Z = a || 1;
		this.S = b || Sb;
		this.ma = y(this.Jb, this);
		this.va = z()
	}
	C(Rb, O);
	Rb.prototype.enabled = n;
	var Sb = r.window;
	q = Rb.prototype;
	q.C = l;
	q.Jb = function ()
	{
		if (this.enabled)
		{
			var a = z() - this.va;
			if (a > 0 && a < this.Z * 0.8) this.C = this.S.setTimeout(this.ma, this.Z - a);
			else if (this.dispatchEvent(Tb), this.enabled) this.C = this.S.setTimeout(this.ma, this.Z), this.va = z()
		}
	};
	q.start = function ()
	{
		this.enabled = k;
		if (!this.C) this.C = this.S.setTimeout(this.ma, this.Z), this.va = z()
	};
	q.stop = function ()
	{
		this.enabled = n;
		if (this.C) this.S.clearTimeout(this.C), this.C = l
	};
	q.a = function ()
	{
		Rb.c.a.call(this);
		this.stop();
		delete this.S
	};
	var Tb = "tick";

	function Ub(a, b)
	{
		w(a) || (a && typeof a.handleEvent == "function" ? a = y(a.handleEvent, a) : g(Error("Invalid listener argument")));
		return b > 2147483647 ? -1 : Sb.setTimeout(a, b || 0)
	};
	var Vb, Wb = !H || eb(),
		Xb = !Ra && !H || H && eb() || Ra && cb("1.9.1");
	H && cb("9");

	function Yb(a)
	{
		return (a = a.className) && typeof a.split == "function" ? a.split(/\s+/) : []
	}
	function P(a, b)
	{
		var c = Yb(a),
			d = Ga(arguments, 1),
			e;
		e = c;
		for (var f = 0, h = 0; h < d.length; h++) Aa(e, d[h]) >= 0 || (e.push(d[h]), f++);
		e = f == d.length;
		a.className = c.join(" ");
		return e
	}
	function Zb(a, b)
	{
		for (var c = Yb(a), d = Ga(arguments, 1), e = c, f = 0, h = 0; h < e.length; h++) Aa(d, e[h]) >= 0 && (Fa(e, h--, 1), f++);
		a.className = c.join(" ")
	}
	function Q(a, b, c)
	{
		c ? P(a, b) : Zb(a, b)
	};

	function $b(a, b)
	{
		this.x = a !== i ? a : 0;
		this.y = b !== i ? b : 0
	}
	$b.prototype.toString = function ()
	{
		return "(" + this.x + ", " + this.y + ")"
	};

	function ac(a, b)
	{
		this.width = a;
		this.height = b
	}
	ac.prototype.toString = function ()
	{
		return "(" + this.width + " x " + this.height + ")"
	};
	ac.prototype.floor = function ()
	{
		this.width = Math.floor(this.width);
		this.height = Math.floor(this.height);
		return this
	};
	ac.prototype.round = function ()
	{
		this.width = Math.round(this.width);
		this.height = Math.round(this.height);
		return this
	};

	function bc(a)
	{
		return a ? new cc(dc(a)) : Vb || (Vb = new cc)
	}
	function ec(a, b)
	{
		var c = b || document;
		if (fc(c)) return c.querySelectorAll("." + a);
		else if (c.getElementsByClassName) return c.getElementsByClassName(a);
		return gc(a, b)
	}
	function hc(a, b)
	{
		var c = b || document,
			d = l;
		return (d = fc(c) ? c.querySelector("." + a) : ec(a, b)[0]) || l
	}
	function fc(a)
	{
		return a.querySelectorAll && a.querySelector && (!Sa || document.compatMode == "CSS1Compat" || cb("528"))
	}

	function gc(a, b)
	{
		var c, d, e, f;
		c = b || document;
		if (fc(c) && a) return c.querySelectorAll("" + (a ? "." + a : ""));
		if (a && c.getElementsByClassName)
		{
			var h = c.getElementsByClassName(a);
			return h
		}
		h = c.getElementsByTagName("*");
		if (a)
		{
			f = {};
			for (d = e = 0; c = h[d]; d++)
			{
				var j = c.className;
				typeof j.split == "function" && Aa(j.split(/\s+/), a) >= 0 && (f[e++] = c)
			}
			f.length = e;
			return f
		}
		else return h
	}

	function ic(a, b)
	{
		Cb(b, function (b, d)
		{
			d == "style" ? a.style.cssText = b : d == "class" ? a.className = b : d == "for" ? a.htmlFor = b : d in jc ? a.setAttribute(jc[d], b) : d.lastIndexOf("aria-", 0) == 0 ? a.setAttribute(d, b) : a[d] = b
		})
	}
	var jc = {
		cellpadding: "cellPadding",
		cellspacing: "cellSpacing",
		colspan: "colSpan",
		rowspan: "rowSpan",
		valign: "vAlign",
		height: "height",
		width: "width",
		usemap: "useMap",
		frameborder: "frameBorder",
		maxlength: "maxLength",
		type: "type"
	};

	function R(a, b, c)
	{
		return kc(document, arguments)
	}

	function kc(a, b)
	{
		var c = b[0],
			d = b[1];
		if (!Wb && d && (d.name || d.type))
		{
			c = ["<", c];
			d.name && c.push(' name="', F(d.name), '"');
			if (d.type)
			{
				c.push(' type="', F(d.type), '"');
				var e = {};
				Hb(e, d);
				d = e;
				delete d.type
			}
			c.push(">");
			c = c.join("")
		}
		c = a.createElement(c);
		if (d) v(d) ? c.className = d : u(d) ? P.apply(l, [c].concat(d)) : ic(c, d);
		b.length > 2 && lc(a, c, b);
		return c
	}

	function lc(a, b, c)
	{
		function d(c)
		{
			c && b.appendChild(v(c) ? a.createTextNode(c) : c)
		}
		for (var e = 2; e < c.length; e++)
		{
			var f = c[e];
			if (da(f) && !(ea(f) && f.nodeType > 0))
			{
				var h;
				a: {
					if (f && typeof f.length == "number") if (ea(f))
					{
						h = typeof f.item == "function" || typeof f.item == "string";
						break a
					}
					else if (w(f))
					{
						h = typeof f.item == "function";
						break a
					}
					h = n
				}
				Ba(h ? Ea(f) : f, d)
			}
			else d(f)
		}
	}
	function mc(a)
	{
		a && a.parentNode && a.parentNode.removeChild(a)
	}

	function nc(a)
	{
		return Xb && a.children != i ? a.children : Ca(a.childNodes, function (a)
		{
			return a.nodeType == 1
		})
	}
	function oc(a, b)
	{
		if (a.contains && b.nodeType == 1) return a == b || a.contains(b);
		if (typeof a.compareDocumentPosition != "undefined") return a == b || Boolean(a.compareDocumentPosition(b) & 16);
		for (; b && a != b;) b = b.parentNode;
		return b == a
	}
	function dc(a)
	{
		return a.nodeType == 9 ? a : a.ownerDocument || a.document
	}
	function pc(a, b)
	{
		for (var c = 3 == l, d = 0; a && (c || d <= 3);)
		{
			if (b(a)) return a;
			a = a.parentNode;
			d++
		}
		return l
	}

	function cc(a)
	{
		this.t = a || r.document || document
	}
	q = cc.prototype;
	q.Sa = bc;
	q.b = function (a)
	{
		return v(a) ? this.t.getElementById(a) : a
	};
	q.oa = function (a, b, c)
	{
		return kc(this.t, arguments)
	};
	q.createElement = function (a)
	{
		return this.t.createElement(a)
	};
	q.createTextNode = function (a)
	{
		return this.t.createTextNode(a)
	};

	function qc(a)
	{
		var b = a.t,
			a = !Sa && b.compatMode == "CSS1Compat" ? b.documentElement : b.body,
			b = b.parentWindow || b.defaultView;
		return new $b(b.pageXOffset || a.scrollLeft, b.pageYOffset || a.scrollTop)
	}
	q.appendChild = function (a, b)
	{
		a.appendChild(b)
	};
	q.contains = oc;
	var Fb = {},
		rc = l;

	function sc(a)
	{
		a = x(a);
		delete Fb[a];
		Eb() && rc && (Sb.clearTimeout(rc), rc = l)
	}
	function tc()
	{
		rc || (rc = Ub(function ()
		{
			uc()
		}, 20))
	}
	function uc()
	{
		var a = z();
		rc = l;
		Cb(Fb, function (b)
		{
			vc(b, a)
		});
		Eb() || tc()
	};

	function wc(a, b, c, d)
	{
		(!u(a) || !u(b)) && g(Error("Start and end parameters must be arrays"));
		a.length != b.length && g(Error("Start and end points must be the same length"));
		this.Q = a;
		this.mb = b;
		this.duration = c;
		this.Fa = d;
		this.coords = []
	}
	C(wc, O);
	q = wc.prototype;
	q.l = 0;
	q.Ra = 0;
	q.k = 0;
	q.startTime = l;
	q.Na = l;
	q.ua = l;
	q.play = function (a)
	{
		if (a || this.l == 0) this.k = 0, this.coords = this.Q;
		else if (this.l == 1) return n;
		sc(this);
		this.startTime = a = z();
		this.l == -1 && (this.startTime -= this.duration * this.k);
		this.Na = this.startTime + this.duration;
		this.ua = this.startTime;
		this.k || this.A();
		S(this, "play");
		this.l == -1 && S(this, "resume");
		this.l = 1;
		var b = x(this);
		b in Fb || (Fb[b] = this);
		tc();
		vc(this, a);
		return k
	};
	q.stop = function (a)
	{
		sc(this);
		this.l = 0;
		if (a) this.k = 1;
		xc(this, this.k);
		S(this, "stop");
		this.H()
	};
	q.pause = function ()
	{
		if (this.l == 1) sc(this), this.l = -1, S(this, "pause")
	};
	q.a = function ()
	{
		this.l != 0 && this.stop(n);
		S(this, "destroy");
		wc.c.a.call(this)
	};

	function vc(a, b)
	{
		a.k = (b - a.startTime) / (a.Na - a.startTime);
		if (a.k >= 1) a.k = 1;
		a.Ra = 1E3 / (b - a.ua);
		a.ua = b;
		xc(a, a.k);
		a.k == 1 ? (a.l = 0, sc(a), S(a, "finish"), a.H()) : a.l == 1 && a.xa()
	}
	function xc(a, b)
	{
		w(a.Fa) && (b = a.Fa(b));
		a.coords = Array(a.Q.length);
		for (var c = 0; c < a.Q.length; c++) a.coords[c] = (a.mb[c] - a.Q[c]) * b + a.Q[c]
	}
	q.xa = function ()
	{
		S(this, "animate")
	};
	q.A = function ()
	{
		S(this, "begin")
	};
	q.H = function ()
	{
		S(this, "end")
	};

	function S(a, b)
	{
		a.dispatchEvent(new yc(b, a))
	}
	function yc(a, b)
	{
		I.call(this, a);
		this.coords = b.coords;
		this.x = b.coords[0];
		this.y = b.coords[1];
		this.Wc = b.coords[2];
		this.duration = b.duration;
		this.k = b.k;
		this.Qc = b.Ra;
		this.state = b.l;
		this.Pc = b
	}
	C(yc, I);

	function zc(a, b)
	{
		var c;
		a: {
			c = dc(a);
			if (c.defaultView && c.defaultView.getComputedStyle && (c = c.defaultView.getComputedStyle(a, l)))
			{
				c = c[b] || c.getPropertyValue(b);
				break a
			}
			c = ""
		}
		return c || (a.currentStyle ? a.currentStyle[b] : l) || a.style[b]
	}
	function Ac(a)
	{
		var b = a.getBoundingClientRect();
		if (H) a = a.ownerDocument, b.left -= a.documentElement.clientLeft + a.body.clientLeft, b.top -= a.documentElement.clientTop + a.body.clientTop;
		return b
	}

	function Bc(a)
	{
		if (H) return a.offsetParent;
		for (var b = dc(a), c = zc(a, "position"), d = c == "fixed" || c == "absolute", a = a.parentNode; a && a != b; a = a.parentNode) if (c = zc(a, "position"), d = d && c == "static" && a != b.documentElement && a != b.body, !d && (a.scrollWidth > a.clientWidth || a.scrollHeight > a.clientHeight || c == "fixed" || c == "absolute" || c == "relative")) return a;
		return l
	}

	function Cc(a)
	{
		var b = new $b;
		if (a.nodeType == 1) if (a.getBoundingClientRect) a = Ac(a), b.x = a.left, b.y = a.top;
		else
		{
			var c = qc(bc(a));
			var d, e = dc(a),
				f = zc(a, "position"),
				h = Ra && e.getBoxObjectFor && !a.getBoundingClientRect && f == "absolute" && (d = e.getBoxObjectFor(a)) && (d.screenX < 0 || d.screenY < 0),
				j = new $b(0, 0),
				m;
			d = e ? e.nodeType == 9 ? e : dc(e) : document;
			if (m = H) if (m = !eb()) m = bc(d).t.compatMode != "CSS1Compat";
			m = m ? d.body : d.documentElement;
			if (a != m) if (a.getBoundingClientRect) d = Ac(a), a = qc(bc(e)), j.x = d.left + a.x, j.y = d.top + a.y;
			else if (e.getBoxObjectFor && !h) d = e.getBoxObjectFor(a), a = e.getBoxObjectFor(m), j.x = d.screenX - a.screenX, j.y = d.screenY - a.screenY;
			else
			{
				h = a;
				do
				{
					j.x += h.offsetLeft;
					j.y += h.offsetTop;
					h != a && (j.x += h.clientLeft || 0, j.y += h.clientTop || 0);
					if (Sa && zc(h, "position") == "fixed")
					{
						j.x += e.body.scrollLeft;
						j.y += e.body.scrollTop;
						break
					}
					h = h.offsetParent
				} while (h && h != a);
				if (Qa || Sa && f == "absolute") j.y -= e.body.offsetTop;
				for (h = a;
				(h = Bc(h)) && h != e.body && h != m;) if (j.x -= h.scrollLeft, !Qa || h.tagName != "TR") j.y -= h.scrollTop
			}
			b.x = j.x - c.x;
			b.y = j.y - c.y
		}
		else c = w(a.ob), j = a, a.targetTouches ? j = a.targetTouches[0] : c && a.u.targetTouches && (j = a.u.targetTouches[0]), b.x = j.clientX, b.y = j.clientY;
		return b
	}
	function Dc(a, b, c)
	{
		b instanceof ac ? (c = b.height, b = b.width) : c == i && g(Error("missing height argument"));
		Ec(a, b);
		a.style.height = Fc(c, k)
	}
	function Fc(a, b)
	{
		typeof a == "number" && (a = (b ? Math.round(a) : a) + "px");
		return a
	}
	function Ec(a, b)
	{
		a.style.width = Fc(b, k)
	}

	function T(a)
	{
		if (zc(a, "display") != "none") return Gc(a);
		var b = a.style,
			c = b.display,
			d = b.visibility,
			e = b.position;
		b.visibility = "hidden";
		b.position = "absolute";
		b.display = "inline";
		a = Gc(a);
		b.display = c;
		b.position = e;
		b.visibility = d;
		return a
	}
	function Gc(a)
	{
		var b = a.offsetWidth,
			c = a.offsetHeight,
			d = Sa && !b && !c;
		return (b === i || d) && a.getBoundingClientRect ? (a = Ac(a), new ac(a.right - a.left, a.bottom - a.top)) : new ac(b, c)
	}

	function Hc(a, b)
	{
		var c = a.style;
		if ("opacity" in c) c.opacity = b;
		else if ("MozOpacity" in c) c.MozOpacity = b;
		else if ("filter" in c) c.filter = b === "" ? "" : "alpha(opacity=" + b * 100 + ")"
	};

	function U(a, b, c, d, e)
	{
		wc.call(this, b, c, d, e);
		this.element = a
	}
	C(U, wc);
	U.prototype.ka = t;
	U.prototype.xa = function ()
	{
		this.ka();
		U.c.xa.call(this)
	};
	U.prototype.H = function ()
	{
		this.ka();
		U.c.H.call(this)
	};
	U.prototype.A = function ()
	{
		this.ka();
		U.c.A.call(this)
	};

	function V(a, b, c, d, e)
	{
		typeof b == "number" && (b = [b]);
		typeof c == "number" && (c = [c]);
		U.call(this, a, b, c, d, e);
		(b.length != 1 || c.length != 1) && g(Error("Start and end points must be 1D"))
	}
	C(V, U);
	V.prototype.ka = function ()
	{
		Hc(this.element, this.coords[0])
	};
	V.prototype.show = function ()
	{
		this.element.style.display = ""
	};

	function Ic(a, b, c)
	{
		V.call(this, a, 1, 0, b, c)
	}
	C(Ic, V);
	Ic.prototype.A = function ()
	{
		this.show();
		Ic.c.A.call(this)
	};
	Ic.prototype.H = function ()
	{
		this.element.style.display = "none";
		Ic.c.H.call(this)
	};

	function Jc(a, b, c)
	{
		V.call(this, a, 0, 1, b, c)
	}
	C(Jc, V);
	Jc.prototype.A = function ()
	{
		this.show();
		Jc.c.A.call(this)
	};

	function Kc(a, b)
	{
		this.aa = {};
		this.f = [];
		var c = arguments.length;
		if (c > 1)
		{
			c % 2 && g(Error("Uneven number of arguments"));
			for (var d = 0; d < c; d += 2) this.set(arguments[d], arguments[d + 1])
		}
		else if (a)
		{
			if (a instanceof Kc) d = Lc(a), c = Mc(a);
			else
			{
				var c = [],
					e = 0;
				for (d in a) c[e++] = d;
				d = c;
				c = Db(a)
			}
			for (e = 0; e < d.length; e++) this.set(d[e], c[e])
		}
	}
	Kc.prototype.j = 0;
	Kc.prototype.Mb = 0;

	function Mc(a)
	{
		Nc(a);
		for (var b = [], c = 0; c < a.f.length; c++) b.push(a.aa[a.f[c]]);
		return b
	}
	function Lc(a)
	{
		Nc(a);
		return a.f.concat()
	}

	function Nc(a)
	{
		if (a.j != a.f.length)
		{
			for (var b = 0, c = 0; b < a.f.length;)
			{
				var d = a.f[b];
				Object.prototype.hasOwnProperty.call(a.aa, d) && (a.f[c++] = d);
				b++
			}
			a.f.length = c
		}
		if (a.j != a.f.length)
		{
			for (var e = {}, c = b = 0; b < a.f.length;) d = a.f[b], Object.prototype.hasOwnProperty.call(e, d) || (a.f[c++] = d, e[d] = 1), b++;
			a.f.length = c
		}
	}
	Kc.prototype.set = function (a, b)
	{
		Object.prototype.hasOwnProperty.call(this.aa, a) || (this.j++, this.f.push(a), this.Mb++);
		this.aa[a] = b
	};

	function Oc(a)
	{
		return Pc(a || arguments.callee.caller, [])
	}

	function Pc(a, b)
	{
		var c = [];
		if (Aa(b, a) >= 0) c.push("[...circular reference...]");
		else if (a && b.length < 50)
		{
			c.push(Qc(a) + "(");
			for (var d = a.arguments, e = 0; e < d.length; e++)
			{
				e > 0 && c.push(", ");
				var f;
				f = d[e];
				switch (typeof f)
				{
				case "object":
					f = f ? "object" : "null";
					break;
				case "string":
					break;
				case "number":
					f = String(f);
					break;
				case "boolean":
					f = f ? "true" : "false";
					break;
				case "function":
					f = (f = Qc(f)) ? f : "[fn]";
					break;
				default:
					f = typeof f
				}
				f.length > 40 && (f = f.substr(0, 40) + "...");
				c.push(f)
			}
			b.push(a);
			c.push(")\n");
			try
			{
				c.push(Pc(a.caller, b))
			}
			catch (h)
			{
				c.push("[exception trying to get caller]\n")
			}
		}
		else a ? c.push("[...long stack...]") : c.push("[end]");
		return c.join("")
	}
	function Qc(a)
	{
		if (Rc[a]) return Rc[a];
		a = String(a);
		if (!Rc[a])
		{
			var b = /function ([^\(]+)/.exec(a);
			Rc[a] = b ? b[1] : "[Anonymous]"
		}
		return Rc[a]
	}
	var Rc = {};

	function Sc(a, b, c, d, e)
	{
		this.reset(a, b, c, d, e)
	}
	Sc.prototype.Hb = 0;
	Sc.prototype.Pa = l;
	Sc.prototype.Oa = l;
	var Tc = 0;
	Sc.prototype.reset = function (a, b, c, d, e)
	{
		this.Hb = typeof e == "number" ? e : Tc++;
		this.Vc = d || z();
		this.L = a;
		this.Bb = b;
		this.Sc = c;
		delete this.Pa;
		delete this.Oa
	};
	Sc.prototype.fb = function (a)
	{
		this.L = a
	};

	function W(a)
	{
		this.Cb = a
	}
	W.prototype.g = l;
	W.prototype.L = l;
	W.prototype.s = l;
	W.prototype.Wa = l;

	function Uc(a, b)
	{
		this.name = a;
		this.value = b
	}
	Uc.prototype.toString = o("name");
	var Vc = new Uc("WARNING", 900),
		Wc = new Uc("CONFIG", 700);
	W.prototype.getParent = o("g");
	W.prototype.fb = function (a)
	{
		this.L = a
	};

	function Xc(a)
	{
		if (a.L) return a.L;
		if (a.g) return Xc(a.g);
		za("Root logger has no level set.");
		return l
	}
	W.prototype.log = function (a, b, c)
	{
		if (a.value >= Xc(this).value)
		{
			a = this.pb(a, b, c);
			b = "log:" + a.Bb;
			r.console && (r.console.timeStamp ? r.console.timeStamp(b) : r.console.markTimeline && r.console.markTimeline(b));
			r.msWriteProfilerMark && r.msWriteProfilerMark(b);
			for (b = this; b;)
			{
				var c = b,
					d = a;
				if (c.Wa) for (var e = 0, f = i; f = c.Wa[e]; e++) f(d);
				b = b.getParent()
			}
		}
	};
	W.prototype.pb = function (a, b, c)
	{
		var d = new Sc(a, String(b), this.Cb);
		if (c)
		{
			d.Pa = c;
			var e;
			var f = arguments.callee.caller;
			try
			{
				var h;
				var j = ba("window.location.href");
				if (v(c)) h = {
					message: c,
					name: "Unknown error",
					lineNumber: "Not available",
					fileName: j,
					stack: "Not available"
				};
				else
				{
					var m, p, D = n;
					try
					{
						m = c.lineNumber || c.Rc || "Not available"
					}
					catch (s)
					{
						m = "Not available", D = k
					}
					try
					{
						p = c.fileName || c.filename || c.sourceURL || j
					}
					catch (A)
					{
						p = "Not available", D = k
					}
					h = D || !c.lineNumber || !c.fileName || !c.stack ? {
						message: c.message,
						name: c.name,
						lineNumber: m,
						fileName: p,
						stack: c.stack || "Not available"
					} : c
				}
				e = "Message: " + F(h.message) + '\nUrl: <a href="view-source:' + h.fileName + '" target="_new">' + h.fileName + "</a>\nLine: " + h.lineNumber + "\n\nBrowser stack:\n" + F(h.stack + "-> ") + "[end]\n\nJS stack traversal:\n" + F(Oc(f) + "-> ")
			}
			catch (B)
			{
				e = "Exception trying to expose exception! You win, we lose. " + B
			}
			d.Oa = e
		}
		return d
	};
	var Yc = {},
		Zc = l;

	function $c(a)
	{
		Zc || (Zc = new W(""), Yc[""] = Zc, Zc.fb(Wc));
		var b;
		if (!(b = Yc[a]))
		{
			b = new W(a);
			var c = a.lastIndexOf("."),
				d = a.substr(c + 1),
				c = $c(a.substr(0, c));
			if (!c.s) c.s = {};
			c.s[d] = b;
			b.g = c;
			Yc[a] = b
		}
		return b
	};

	function X(a)
	{
		this.wb = a;
		this.f = []
	}
	C(X, E);
	var ad = [];

	function bd(a, b, c, d)
	{
		u(c) || (ad[0] = c, c = ad);
		for (var e = 0; e < c.length; e++) a.f.push(M(b, c[e], d || a, n, a.wb || a))
	}
	function cd(a)
	{
		Ba(a.f, Lb);
		a.f.length = 0
	}
	X.prototype.a = function ()
	{
		X.c.a.call(this);
		cd(this)
	};
	X.prototype.handleEvent = function ()
	{
		g(Error("EventHandler.handleEvent not implemented"))
	};

	function dd()
	{}(function (a)
	{
		a.Ta = function ()
		{
			return a.yb || (a.yb = new a)
		}
	})(dd);
	dd.prototype.Db = 0;
	dd.Ta();

	function Y(a)
	{
		this.F = a || bc();
		this.Gb = ed
	}
	C(Y, O);
	Y.prototype.xb = dd.Ta();
	var ed = l;
	q = Y.prototype;
	q.X = l;
	q.v = n;
	q.d = l;
	q.Gb = l;
	q.Ab = l;
	q.g = l;
	q.s = l;
	q.r = l;
	q.ib = n;

	function fd(a)
	{
		return a.X || (a.X = ":" + (a.xb.Db++).toString(36))
	}
	q.b = o("d");
	q.getParent = o("g");
	q.ja = function (a)
	{
		this.g && this.g != a && g(Error("Method not supported"));
		Y.c.ja.call(this, a)
	};
	q.Sa = o("F");
	q.oa = function ()
	{
		this.d = this.F.createElement("div")
	};
	q.Aa = function (a, b)
	{
		this.v && g(Error("Component already rendered"));
		this.d || this.oa();
		a ? a.insertBefore(this.d, b || l) : this.F.t.body.appendChild(this.d);
		(!this.g || this.g.v) && this.K()
	};
	q.p = function (a)
	{
		if (this.v) g(Error("Component already rendered"));
		else if (a)
		{
			this.ib = k;
			if (!this.F || this.F.t != dc(a)) this.F = bc(a);
			this.d = a;
			this.K()
		}
		else g(Error("Invalid element to decorate"))
	};
	q.K = function ()
	{
		this.v = k;
		gd(this, function (a)
		{
			!a.v && a.b() && a.K()
		})
	};

	function hd(a)
	{
		gd(a, function (a)
		{
			a.v && hd(a)
		});
		a.W && cd(a.W);
		a.v = n
	}
	q.a = function ()
	{
		Y.c.a.call(this);
		this.v && hd(this);
		this.W && (this.W.h(), delete this.W);
		gd(this, function (a)
		{
			a.h()
		});
		!this.ib && this.d && mc(this.d);
		this.g = this.Ab = this.d = this.r = this.s = l
	};

	function gd(a, b)
	{
		a.s && Ba(a.s, b, i)
	}
	q.removeChild = function (a, b)
	{
		if (a)
		{
			var c = v(a) ? a : fd(a),
				a = this.r && c ? (c in this.r ? this.r[c] : i) || l : l;
			if (c && a)
			{
				var d = this.r;
				c in d && delete d[c];
				c = this.s;
				d = Aa(c, a);
				d >= 0 && G.splice.call(c, d, 1);
				b && (hd(a), a.d && mc(a.d));
				c = a;
				c == l && g(Error("Unable to set parent component"));
				c.g = l;
				Y.c.ja.call(c, l)
			}
		}
		a || g(Error("Child is not in parent component"));
		return a
	};
	var id;
	(function ()
	{
		function a(a)
		{
			a = a.match(/[\d]+/g);
			a.length = 3;
			return a.join(".")
		}
		var b = n,
			c = "";
		if (navigator.plugins && navigator.plugins.length)
		{
			var d = navigator.plugins["Shockwave Flash"];
			d && (b = k, d.description && (c = a(d.description)));
			navigator.plugins["Shockwave Flash 2.0"] && (b = k, c = "2.0.0.11")
		}
		else if (navigator.mimeTypes && navigator.mimeTypes.length)(b = (d = navigator.mimeTypes["application/x-shockwave-flash"]) && d.enabledPlugin) && (c = a(d.enabledPlugin.description));
		else try
		{
			d = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7"), b = k, c = a(d.GetVariable("$version"))
		}
		catch (e)
		{
			try
			{
				d = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6"), b = k, c = "6.0.21"
			}
			catch (f)
			{
				try
				{
					d = new ActiveXObject("ShockwaveFlash.ShockwaveFlash"), b = k, c = a(d.GetVariable("$version"))
				}
				catch (h)
				{}
			}
		}
		id = c
	})();

	function jd(a, b)
	{
		Y.call(this, b);
		this.nb = a;
		this.pa = new X(this);
		this.qa = new Kc
	}
	C(jd, Y);
	q = jd.prototype;
	q.zb = $c("goog.ui.media.FlashObject");
	q.jb = "window";
	q.kb = "#000000";
	q.Ga = "sameDomain";

	function kd(a, b, c)
	{
		a.Da = v(b) ? b : Math.round(b) + "px";
		a.ta = v(c) ? c : Math.round(c) + "px";
		a.b() && Dc(a.b() ? a.b().firstChild : l, a.Da, a.ta)
	}
	q.K = function ()
	{
		jd.c.K.call(this);
		var a = this.b(),
			b;
		b = H ? '<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" id="%s" name="%s" class="%s"><param name="movie" value="%s"/><param name="quality" value="high"/><param name="FlashVars" value="%s"/><param name="bgcolor" value="%s"/><param name="AllowScriptAccess" value="%s"/><param name="allowFullScreen" value="true"/><param name="SeamlessTabbing" value="false"/>%s</object>' : '<embed quality="high" id="%s" name="%s" class="%s" src="%s" FlashVars="%s" bgcolor="%s" AllowScriptAccess="%s" allowFullScreen="true" SeamlessTabbing="false" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" %s></embed>';
		for (var c = H ? '<param name="wmode" value="%s"/>' : "wmode=%s", c = ma(c, this.jb), d = Lc(this.qa), e = Mc(this.qa), f = [], h = 0; h < d.length; h++)
		{
			var j = oa(d[h]),
				m = oa(e[h]);
			f.push(j + "=" + m)
		}
		b = ma(b, fd(this), fd(this), "goog-ui-media-flash-object", F(this.nb), F(f.join("&")), this.kb, this.Ga, c);
		a.innerHTML = b;
		this.Da && this.ta && kd(this, this.Da, this.ta);
		bd(this.pa, this.b(), Db(hb), gb)
	};
	q.oa = function ()
	{
		this.eb != l && !(ua(id, this.eb) >= 0) && (this.zb.log(Vc, "Required flash version not found:" + this.eb, i), g(Error("Method not supported")));
		var a = this.Sa().createElement("div");
		a.className = "goog-ui-media-flash";
		this.d = a
	};
	q.a = function ()
	{
		jd.c.a.call(this);
		this.qa = l;
		this.pa.h();
		this.pa = l
	};

	function Z(a)
	{
		this.U = v(a) ? document.getElementById(a) : a;
		r._players_ || (r._players_ = {});
		a = "player_" + (ld++).toString(36) + z().toString(36);
		r._players_[a] = this;
		this.ea = a;
		this.Aa();
		r.onYouTubePlayerReady = r.onYouTubePlayerReady || md;
		M(this.wa, Tb, this.Kb, n, this);
		M(this.za, Tb, this.Lb, n, this);
		M(this.da, "click", this.hb, n, this);
		M(this.O, "click", this.gb, n, this);
		M(this.O, "mouseover", this.qb, n, this);
		M(this.D, "mouseout", this.vb, n, this);
		M(this.T, "click", this.ub, n, this);
		M(this.ha, "click", this.Va, n, this);
		M(this.ia, "click", this.Va, n, this);
		a = ma("http://www.youtube.com/apiplayer?enablejsapi=1&version=3&playerapiid=%s", this.ea);
		a = new jd(a);
		a.Ga = "always";
		a.jb = "transparent";
		var b = this.ea;
		if (a.g && a.g.r)
		{
			var c = a.g.r,
				d = a.X;
			d in c && delete c[d];
			c = a.g.r;
			b in c && g(Error('The object already contains the key "' + b + '"'));
			c[b] = a
		}
		a.X = b;
		b = T(this.N);
		kd(a, b.width, b.height);
		a.Aa(this.N)
	}
	aa("gweb.ui.YTPlayer", Z);
	var ld = 0;
	q = Z.prototype;
	q.N = l;
	q.da = l;
	q.ha = l;
	q.ia = l;
	q.ga = l;
	q.O = l;
	q.na = l;
	q.la = l;
	q.T = l;
	q.D = l;
	q.ya = l;
	q.wa = l;
	q.za = l;
	q.Aa = function ()
	{
		this.N = R("DIV", "gweb-ytplayer-movie");
		this.da = R("DIV", "gweb-ytplayer-play");
		this.ha = R("DIV", "gweb-ytplayer-scrub-loading");
		this.ia = R("DIV", "gweb-ytplayer-scrub-playing");
		this.ga = R("DIV", "gweb-ytplayer-scrub", this.ha, this.ia);
		this.O = R("DIV", "gweb-ytplayer-mute");
		this.na = R("DIV", "gweb-ytplayer-controls", this.da, this.ga, this.O);
		this.la = R("DIV", "gweb-ytplayer-volume-bar-level");
		this.T = R("DIV", "gweb-ytplayer-volume-bar", this.la);
		this.D = R("DIV", "gweb-ytplayer-volume", this.T);
		this.ya = R("DIV", "gweb-ytplayer", this.N, this.na, this.D);
		this.U.style.padding = 0;
		this.U.innerHTML = "";
		this.U.appendChild(this.ya);
		var a = T(this.U),
			b = a.width,
			c = a.height;
		Dc(this.ya, a);
		Dc(this.N, b, c - 30);
		Dc(this.na, b, 30);
		Ec(this.ga, b - 57);
		Hc(this.D, 0);
		this.Ob = new Jc(this.D, 100);
		this.Pb = new Ic(this.D, 100);
		this.J = T(this.T).height;
		this.Ba = T(this.ga).width;
		this.wa = new Rb(3);
		this.za = new Rb(3)
	};

	function md(a)
	{
		r._players_[a].bb()
	}
	q.qb = function ()
	{
		this.Ob.play()
	};
	q.vb = function (a)
	{
		var b = this.D;
		pc(a.relatedTarget, function (a)
		{
			return a == b
		}) || this.Pb.play()
	};
	q.ub = function (a)
	{
		var b = 0;
		a.target == this.la && (b = this.J - T(a.target).height);
		this.Ca((this.J - (a.offsetY + b)) / this.J * 100)
	};
	q.Va = function (a)
	{
		this.e.seekTo(a.offsetX / this.Ba * this.Nb);
		this.e.getPlayerState()
	};
	q.hb = function ()
	{
		this.e.getPlayerState() == 1 ? this.pause() : this.play()
	};
	Z.prototype.togglePlayPause = Z.prototype.hb;
	Z.prototype.play = function ()
	{
		this.e.playVideo()
	};
	Z.prototype.play = Z.prototype.play;
	Z.prototype.pause = function ()
	{
		this.e.pauseVideo()
	};
	Z.prototype.pause = Z.prototype.pause;
	Z.prototype.gb = function ()
	{
		var a = this.e.isMuted();
		a ? this.e.unMute() : this.e.mute();
		Q(this.O, "gweb-ytplayer-muted", !a)
	};
	Z.prototype.toggleMute = Z.prototype.gb;
	Z.prototype.bb = function ()
	{
		this.e = document.getElementById(this.ea);
		this.e.addEventListener("onStateChange", ["_players_", this.ea, "onPlayerStateChange"].join("."));
		this.Ca();
		this.wa.start();
		this.za.start()
	};
	Z.prototype.playerReady = Z.prototype.bb;
	Z.prototype.ab = function (a, b)
	{
		this.e ? (this.e.clearVideo(), this.e[b ? "loadVideoById" : "cueVideoById"](a)) : Ub(y(arguments.callee, this, a, b), 3)
	};
	Z.prototype.playVideoById = Z.prototype.ab;
	Z.prototype.Eb = function (a)
	{
		Q(this.da, "gweb-ytplayer-playing", a == 1)
	};
	Z.prototype.onPlayerStateChange = Z.prototype.Eb;
	Z.prototype.Ca = function (a)
	{
		this.J = this.J || T(this.T).height;
		a = a || this.e.getVolume();
		this.e.setVolume(a);
		this.la.style.height = Fc(a / 100 * this.J, k)
	};
	Z.prototype.setVolume = Z.prototype.Ca;
	Z.prototype.Kb = function ()
	{
		if (this.e)
		{
			var a = this.e.getVideoBytesLoaded(),
				b = this.e.getVideoBytesTotal();
			b && Ec(this.ha, a / b * this.Ba)
		}
	};
	Z.prototype.Lb = function ()
	{
		if (this.e)
		{
			var a = this.e.getCurrentTime(),
				b = this.e.getDuration();
			if (b) Ec(this.ia, a / b * this.Ba), this.Nb = b
		}
	};

	function $(a)
	{
		this.i = new X(this);
		this.d = a;
		this.V()
	}
	C($, O);
	var nd = N("enter"),
		od = N("leave"),
		pd = N("action");
	$.prototype.a = function ()
	{
		$.c.a.call(this);
		cd(this.i);
		this.i.h();
		delete this.d
	};
	$.prototype.ra = function (a)
	{
		switch (a.type)
		{
		case "mouseover":
			var b = nd;
		case "mouseout":
			b = b || od;
			if (!Boolean(a.relatedTarget) || !oc(this.b(), a.relatedTarget)) b = new qd(b, this, a), this.dispatchEvent(b);
			break;
		case "click":
			b = new qd(pd, this, a), this.dispatchEvent(b)
		}
		a.stopPropagation()
	};
	$.prototype.V = function ()
	{
		bd(this.i, this.b(), ["mouseover", "mouseout", "click"], this.ra)
	};
	$.prototype.b = o("d");

	function rd()
	{
		this.i = new X(this);
		this.R = []
	}
	C(rd, O);
	var sd = N("focus"),
		td = N("blur"),
		ud = N("action");
	q = rd.prototype;
	q.a = function ()
	{
		rd.c.a.call(this);
		cd(this.i);
		this.i.h();
		this.n.h();
		this.Qa.h();
		delete this.R
	};
	q.V = function (a, b)
	{
		bd(this.i, a, [nd, od, pd], b)
	};
	q.sb = function (a)
	{
		switch (a.type)
		{
		case nd:
			this.setActive(k);
			break;
		case od:
			this.setActive(n);
			break;
		case pd:
			this.dispatchEvent(new qd(ud, this))
		}
	};
	q.tb = function (a)
	{
		var b = a.target;
		switch (a.type)
		{
		case nd:
			vd(this, b);
			break;
		case od:
			Boolean(a.Ha.relatedTarget) && oc(this.n.b(), a.Ha.relatedTarget) && a.stopPropagation()
		}
	};
	q.reset = function ()
	{
		this.R.length > 0 && vd(this, this.R[0]);
		Dc(this.n.b(), 0, 0)
	};
	q.setActive = function (a)
	{
		Q(this.n.b(), "reticle-active", a);
		this.dispatchEvent(a ? sd : td)
	};

	function vd(a, b)
	{
		var c = T(b.b());
		Dc(a.n.b(), c);
		var c = Bc(b.b()),
			d = Cc(b.b()),
			e = Cc(c),
			c = a.n.b(),
			f = new $b(d.x - e.x, d.y - e.y),
			e = Ra && (La || Ua) && cb("1.9");
		f instanceof $b ? (d = f.x, f = f.y) : (d = f, f = i);
		c.style.left = Fc(d, e);
		c.style.top = Fc(f, e);
		a.Qa = b
	}
	q.b = function ()
	{
		return this.n.b()
	};
	q.p = function (a)
	{
		this.n = new $(a);
		this.V(this.n, this.sb);
		P(a, "reticle")
	};

	function qd(a, b, c)
	{
		I.call(this, a, b);
		this.Ha = c || l
	}
	C(qd, I);

	function wd()
	{
		this.i = new X(this);
		this.G = [];
		this.Ua = k
	}
	C(wd, O);
	var xd = N("update_selected"),
		yd = N("focus"),
		zd = N("blur");
	q = wd.prototype;
	q.a = function ()
	{
		wd.c.a.call(this);
		cd(this.i);
		this.i.h();
		this.G = this.d = l
	};
	q.ra = function (a, b)
	{
		if (this.Ua) switch (b.type)
		{
		case "mouseover":
			var c = k;
		case "mouseout":
			c = c || n;
			Boolean(b.relatedTarget) && oc(this.G[a], b.relatedTarget) || Ad(this, a, c);
			break;
		case "click":
			this.P(a)
		}
	};
	q.b = o("d");

	function Ad(a, b, c)
	{
		Q(a.G[b], "marquee-nav-focus", c);
		a.dispatchEvent(new Bd(c ? yd : zd, a, b))
	}
	q.P = function (a)
	{
		if (this.q != a) this.q >= 0 && Zb(this.G[this.q], "marquee-nav-active"), P(this.G[a], "marquee-nav-active"), this.q = a, this.dispatchEvent(new Bd(xd, this, a))
	};
	q.p = function (a)
	{
		this.d = a;
		for (var b = nc(a), c = 0, d; d = b[c]; c++) bd(this.i, d, ["mouseover", "mouseout", "click"], ja(this.ra, c)), this.G.push(d);
		P(a, "marquee-nav")
	};

	function Bd(a, b, c)
	{
		I.call(this, a, b);
		this.index = c
	}
	C(Bd, I);

	function Cd()
	{
		this.Ea = 0;
		this.w = []
	}
	C(Cd, O);
	q = Cd.prototype;
	q.a = function ()
	{
		Cd.c.a.call(this);
		cd(this.i);
		this.i.h()
	};

	function Dd(a, b)
	{
		a.style.webkitTransform = "translateZ(" + b + "px)"
	}
	function Ed(a, b)
	{
		if (a.w)
		{
			for (var c = 0, d; d = a.w[c]; c++) Dd(d, b * c);
			a.q && Dd(a.Ja, b * a.q * -1)
		}
		a.Ea = b
	}
	q.P = function (a)
	{
		if (this.q != a)
		{
			Dd(this.Ja, this.Ea * a * -1);
			var b = this.q;
			if (this.q >= 0)
			{
				var c = Math.min(b, a),
					d = Math.max(b, a);
				c += 1;
				for (var e;
				(e = this.w[c]) && c < d; c++) Zb(e, "marquee3d-transition"), Ub(ja(P, e, "marquee3d-transition"), 0);
				Zb(this.w[b], "marquee3d-active")
			}
			P(this.w[a], "marquee3d-active");
			this.q = a
		}
	};
	q.setActive = function (a)
	{
		Q(this.b(), "marquee3d-active", a)
	};
	q.b = o("d");
	q.p = function (a)
	{
		P(a, "marquee3d");
		this.d = a;
		if (a.firstElementChild != i) a = a.firstElementChild;
		else for (a = a.firstChild; a && a.nodeType != 1;) a = a.nextSibling;
		if (a = this.Ja = a) for (var a = nc(a), b = 0, c; c = a[b]; b++) Dd(c, this.Ea * b), this.w.push(c)
	};

	function Fd(a)
	{
		var b = a.target.Qa.b();
		switch (a.type)
		{
		case sd:
			var c = k;
		case td:
			Q(b, "tenet-focus", c || n);
			break;
		case ud:
			window.location.href = b.dataset ? b.dataset.href : b.getAttribute("data-" + xa("href"))
		}
	}
	aa("android.page.decorateTenets", function (a)
	{
		var b = hc("reticle", a),
			a = ec("tenet", a),
			c = new rd;
		c.p(b);
		for (var b = 0, d; d = a[b]; b++)
		{
			var e = new $(d);
			d = c;
			e.ja(d.n);
			d.R.push(e);
			d.V(e, d.tb)
		}
		M(c, [sd, td, ud], Fd);
		vd(c, c.R[0]);
		Q(c.n.b(), "reticle-disable", n)
	});

	function Gd()
	{
		this.i = new X(this)
	}
	Gd.prototype.rb = function (a)
	{
		var b = a.index;
		switch (a.type)
		{
		case yd:
			var c = k;
		case zd:
			Ad(this.Ya, b, c || n);
			break;
		case xd:
			this.Ya.P(b);
			this.ba.P(b);
			try
			{
				b < this.ba.w.length - 1 && this.Qb.pause()
			}
			catch (d)
			{}
		}
	};
	Gd.prototype.Ib = function (a)
	{
		this.ba && Ed(this.ba, a)
	};
	Gd.prototype.p = function (a)
	{
		this.d = a;
		var b = hc("home-marquee", a),
			c = this.ba = new Cd;
		Ed(c, -400);
		c.p(b);
		b = v("home-marquee-player") ? document.getElementById("home-marquee-player") : "home-marquee-player";
		c = b.dataset ? b.dataset.videoId : b.getAttribute("data-" + xa("videoId"));
		(this.Qb = new Z(b)).ab(c);
		b = hc("home-nav-view", a);
		c = this.Ya = new wd;
		c.Ua = n;
		c.p(b);
		a = hc("home-nav-mask", a);
		b = this.Tc = new wd;
		b.p(a);
		bd(this.i, b, [yd, zd, xd], this.rb);
		b.P(0)
	};
	aa("android.HomePage", Gd);
	aa("android.HomePage.prototype.decorate", Gd.prototype.p);
	aa("android.HomePage.prototype.setMarqueeItemOffset", Gd.prototype.Ib);
})();