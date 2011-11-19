var cheddar = cheddar || {};
var included_files = [];
cheddar.global = this;
window.requestAnimFrame = (function ()
{
	return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame ||
	function (callback, element)
	{
		window.setTimeout(callback, 1000 / 60);
	};
})();
if (!Array.prototype.deleteFirst)
{
	Array.prototype.deleteFirst = function (obj)
	{
		for (var i = 0, ii = this.length; i < ii; i++)
		{
			if (this[i] == obj)
			{
				this.splice(i, 1);
				return true;
			}
		}
		return false;
	};
}
if (!Array.prototype.stableSort)
{
	Array.prototype.stableSort = function (cmp)
	{
		for (var i = 0, ii = this.length; i < ii; i++)
		{
			this[i].__arrayPos = i;
		}
		return this.sort(Array.__stableSorter(cmp));
	};
	Array.__stableSorter = function (cmp)
	{
		return (function (c1, c2)
		{
			var r = cmp(c1, c2);
			if (!r)
			{
				return c1.__arrayPos - c2.__arrayPos;
			}
			return r;
		});
	};
}
Array.prototype.equals = function (array)
{
	if (!array) return false;
	if (this.length != array.length) return false;
	for (var i = 0, ii = this.length; i < ii; i++)
	{
		var a = this[i];
		var b = array[i];
		if (a.equals && typeof (a.equals) == 'function')
		{
			if (!a.equals(b)) return false;
		}
		else if (a != b)
		{
			return false;
		}
	}
	return true;
};
Array.prototype.rotate = function (backToFront)
{
	if (backToFront)
	{
		this.unshift(this.pop());
		return this[0];
	}
	else
	{
		this.push(this.shift());
		return this[this.length - 1];
	}
};
Array.prototype.pick = function ()
{
	return this[Math.floor(Math.random() * this.length)];
};
Array.prototype.flatten = function ()
{
	var a = [];
	for (var i = 0, ii = this.length; i < ii; i++)
	{
		var e = this[i];
		if (e.flatten)
		{
			var ef = e.flatten();
			for (var j = 0; j < ef.length; j++)
			{
				a[a.length] = ef[j];
			}
		}
		else
		{
			a[a.length] = e;
		}
	}
	return a;
};
Array.prototype.take = function ()
{
	var a = [];
	for (var i = 0, ii = this.length; i < ii; i++)
	{
		var e = [];
		for (var j = 0; j < arguments.length; j++)
		{
			e[j] = this[i][arguments[j]];
		}
		a[i] = e;
	}
	return a;
};
if (!Array.prototype.pluck)
{
	Array.prototype.pluck = function (key)
	{
		var a = [];
		for (var i = 0, ii = this.length; i < ii; i++)
		{
			a[i] = this[i][key];
		}
		return a;
	};
}
Array.prototype.set = function (key, value)
{
	for (var i = 0, ii = this.length; i < ii; i++)
	{
		this[i][key] = value;
	}
};
Array.prototype.allWith = function ()
{
	var a = [];
	topLoop: for (var i = 0, ii = this.length; i < ii; i++)
	{
		var e = this[i];
		for (var j = 0; j < arguments.length; j++)
		{
			if (!this[i][arguments[j]]);
			continue topLoop;
		}
		a[a.length] = e;
	}
	return a;
};
if (!Function.prototype.bind)
{
	Function.prototype.bind = function (object)
	{
		var t = this;
		return function ()
		{
			return t.apply(object, arguments);
		};
	};
}
if (!Array.prototype.last)
{
	Array.prototype.last = function ()
	{
		return this[this.length - 1];
	};
}
if (!Array.prototype.indexOf)
{
	Array.prototype.indexOf = function (obj)
	{
		for (var i = 0, ii = this.length; i < ii; i++)
		{
			if (obj == this[i]) return i;
		}
		return -1;
	};
}
if (!Array.prototype.includes)
{
	Array.prototype.includes = function (obj)
	{
		return (this.indexOf(obj) >= 0);
	};
}
Array.prototype.map = function (f)
{
	var na = new Array(this.length);
	if (f)
	{
		for (var i = 0, ii = this.length; i < ii; i++) na[i] = f(this[i], i, this);
	}
	else
	{
		for (var i = 0, ii = this.length; i < ii; i++) na[i] = this[i];
	}
	return na;
};
if (!Array.prototype.forEach)
{
	Array.prototype.forEach = function (f)
	{
		for (var i = 0, ii = this.length; i < ii; i++) f(this[i], i, this);
	};
}
if (!Array.prototype.reduce)
{
	Array.prototype.reduce = function (f, s)
	{
		var i = 0;
		if (arguments.length == 1)
		{
			s = this[0];
			i++;
		}
		for (var i = 0, ii = this.length; i < ii; i++)
		{
			s = f(s, this[i], i, this);
		}
		return s;
	};
}
if (!Array.prototype.find)
{
	Array.prototype.find = function (f)
	{
		for (var i = 0, ii = this.length; i < ii; i++)
		{
			if (f(this[i], i, this)) return this[i];
		}
	};
}
if (!String.prototype.capitalize)
{
	String.prototype.capitalize = function ()
	{
		return this.replace(/^./, this.slice(0, 1).toUpperCase());
	};
}
if (!String.prototype.escape)
{
	String.prototype.escape = function ()
	{
		return '"' + this.replace(/"/g, '\\"') + '"';
	};
}
if (!String.prototype.splice)
{
	String.prototype.splice = function (start, count, replacement)
	{
		return this.slice(0, start) + replacement + this.slice(start + count);
	};
}
if (!String.prototype.strip)
{
	String.prototype.strip = function ()
	{
		return this.replace(/^\s+|\s+$/g, '');
	};
}
if (!window['$A'])
{
	$A = function (obj)
	{
		var a = new Array(obj.length);
		for (var i = 0; i < obj.length; i++)
		a[i] = obj[i];
		return a;
	};
}
if (!window['$'])
{
	$ = function (id)
	{
		return document.getElementById(id);
	};
}
T = function (text)
{
	return document.createTextNode(text);
};
Object.forceExtend = function (dst, src)
{
	for (var i in src)
	{
		try
		{
			dst[i] = src[i]
		}
		catch (e)
		{}
	}
	return dst;
};
if (!Object.extend) Object.extend = Object.forceExtend;
Object.conditionalExtend = function (dst, src)
{
	for (var i in src)
	{
		if (dst[i] == null) dst[i] = src[i];
	}
	return dst;
};
Object.clone = function (src)
{
	if (!src || src == true) return src;
	switch (typeof (src))
	{
	case 'string':
		return Object.extend(src + '', src);
		break;
	case 'number':
		return src;
		break;
	case 'function':
		obj = eval(src.toSource());
		return Object.extend(obj, src);
		break;
	case 'object':
		if (src instanceof Array)
		{
			return Object.extend([], src);
		}
		else
		{
			return Object.extend(
			{}, src);
		}
		break;
	}
};
Object.loadImage = function (src, onload)
{
	var img = new Image();
	if (onload) img.onload = onload;
	img.src = src;
	return img;
};
Object.isImageLoaded = function (image)
{
	if (image.tagName == 'CANVAS') return true;
	if (!image.complete) return false;
	if (image.naturalWidth == null) return true;
	return !!image.naturalWidth;
};
Object.sum = function (a, b)
{
	if (a instanceof Array)
	{
		if (b instanceof Array)
		{
			var ab = [];
			for (var i = 0; i < a.length; i++)
			{
				ab[i] = a[i] + b[i];
			}
			return ab;
		}
		else
		{
			return a.map(function (v)
			{
				return v + b
			});
		}
	}
	else if (b instanceof Array)
	{
		return b.map(function (v)
		{
			return v + a
		});
	}
	else
	{
		return a + b;
	}
};
Object.sub = function (a, b)
{
	if (a instanceof Array)
	{
		if (b instanceof Array)
		{
			var ab = [];
			for (var i = 0; i < a.length; i++)
			{
				ab[i] = a[i] - b[i];
			}
			return ab;
		}
		else
		{
			return a.map(function (v)
			{
				return v - b
			});
		}
	}
	else if (b instanceof Array)
	{
		return b.map(function (v)
		{
			return a - v
		});
	}
	else
	{
		return a - b;
	}
};
cheddar.Class = function ()
{
	var c = function ()
		{
			this.initialize.apply(this, arguments);
		};
	c.ancestors = $A(arguments);
	c.prototype = {};
	for (var i = 0; i < arguments.length; i++)
	{
		var a = arguments[i];
		if (a.prototype)
		{
			Object.extend(c.prototype, a.prototype);
		}
		else
		{
			Object.extend(c.prototype, a);
		}
	}
	Object.extend(c, c.prototype);
	return c;
};
E = function (name, params, config)
{
	var el = document.createElement(name);
	if (params)
	{
		if (typeof (params) == 'string')
		{
			el.innerHTML = params;
			params = config;
		}
		else if (params.DOCUMENT_NODE)
		{
			el.appendChild(params);
			params = config;
		}
		if (params)
		{
			if (params.style)
			{
				var style = params.style;
				params = Object.clone(params);
				delete params.style;
				Object.forceExtend(el.style, style);
			}
			if (params.content)
			{
				if (typeof (params.content) == 'string')
				{
					el.appendChild(T(params.content));
				}
				else
				{
					el.appendChild(params.content);
				}
				params = Object.clone(params);
				delete params.content;
			}
			Object.forceExtend(el, params);
		}
	}
	return el;
};
E.append = function (node)
{
	for (var i = 1; i < arguments.length; i++)
	{
		if (typeof (arguments[i]) == 'string')
		{
			node.appendChild(T(arguments[i]));
		}
		else
		{
			node.appendChild(arguments[i]);
		}
	}
};
E.lastCanvasId = 0;
E.canvas = function (w, h, config)
{
	var id = 'canvas-uuid-' + E.lastCanvasId;
	E.lastCanvasId++;
	if (!config) config = {};
	return E('canvas', Object.extend(config, {
		id: id,
		width: w,
		height: h
	}));
};
CanvasSupport = {
	DEVICE_SPACE: 0,
	USER_SPACE: 1,
	isPointInPathMode: null,
	supportsIsPointInPath: null,
	supportsCSSTransform: null,
	supportsCanvas: null,
	isCanvasSupported: function ()
	{
		if (this.supportsCanvas == null)
		{
			var e = {};
			try
			{
				e = E('canvas');
			}
			catch (x)
			{}
			this.supportsCanvas = (e.getContext != null);
		}
		return this.supportsCanvas;
	},
	isCSSTransformSupported: function ()
	{
		if (this.supportsCSSTransform == null)
		{
			var e = E('div');
			var dbs = e.style;
			var s = (dbs.webkitTransform != null || dbs.MozTransform != null);
			this.supportsCSSTransform = (s != null);
		}
		return this.supportsCSSTransform;
	},
	getTestContext: function ()
	{
		if (!this.testContext)
		{
			var c = E.canvas(1, 1);
			this.testContext = c.getContext('2d');
		}
		return this.testContext;
	},
	ContextSetterAugment: {
		setFillStyle: function (fs)
		{
			this.fillStyle = fs
		},
		setStrokeStyle: function (ss)
		{
			this.strokeStyle = ss
		},
		setGlobalAlpha: function (ga)
		{
			this.globalAlpha = ga
		},
		setLineWidth: function (lw)
		{
			this.lineWidth = lw
		},
		setLineCap: function (lw)
		{
			this.lineCap = lw
		},
		setLineJoin: function (lw)
		{
			this.lineJoin = lw
		},
		setMiterLimit: function (lw)
		{
			this.miterLimit = lw
		},
		setGlobalCompositeOperation: function (lw)
		{
			this.globalCompositeOperation = lw;
		},
		setShadowColor: function (x)
		{
			this.shadowColor = x
		},
		setShadowBlur: function (x)
		{
			this.shadowBlur = x
		},
		setShadowOffsetX: function (x)
		{
			this.shadowOffsetX = x
		},
		setShadowOffsetY: function (x)
		{
			this.shadowOffsetY = x
		},
		setMozTextStyle: function (x)
		{
			this.mozTextStyle = x
		},
		setFont: function (x)
		{
			this.font = x
		},
		setTextAlign: function (x)
		{
			this.textAlign = x
		},
		setTextBaseline: function (x)
		{
			this.textBaseline = x
		}
	},
	ContextJSImplAugment: {
		identity: function ()
		{
			CanvasSupport.setTransform(this, [1, 0, 0, 1, 0, 0]);
		}
	},
	augment: function (ctx)
	{
		Object.conditionalExtend(ctx, this.ContextSetterAugment);
		Object.conditionalExtend(ctx, this.ContextJSImplAugment);
		return ctx;
	},
	getContext: function (canvas, type)
	{
		var ctx = canvas.getContext(type || '2d');
		this.augment(ctx);
		return ctx;
	},
	tMatrixMultiply: function (m1, m2)
	{
		var m11 = m1[0] * m2[0] + m1[2] * m2[1];
		var m12 = m1[1] * m2[0] + m1[3] * m2[1];
		var m21 = m1[0] * m2[2] + m1[2] * m2[3];
		var m22 = m1[1] * m2[2] + m1[3] * m2[3];
		var dx = m1[0] * m2[4] + m1[2] * m2[5] + m1[4];
		var dy = m1[1] * m2[4] + m1[3] * m2[5] + m1[5];
		m1[0] = m11;
		m1[1] = m12;
		m1[2] = m21;
		m1[3] = m22;
		m1[4] = dx;
		m1[5] = dy;
		return m1;
	},
	tMatrixMultiplyPoint: function (m, x, y)
	{
		return [x * m[0] + y * m[2] + m[4], x * m[1] + y * m[3] + m[5]];
	},
	tInvertMatrix: function (m)
	{
		var d = 1 / (m[0] * m[3] - m[1] * m[2]);
		return [m[3] * d, -m[1] * d, -m[2] * d, m[0] * d, d * (m[2] * m[5] - m[3] * m[4]), d * (m[1] * m[4] - m[0] * m[5])];
	},
	transform: function (ctx, m)
	{
		if (ctx.transform) return ctx.transform.apply(ctx, m);
		ctx.translate(m[4], m[5]);
		if (Math.abs(m[1]) < 1e-6 && Math.abs(m[2]) < 1e-6)
		{
			ctx.scale(m[0], m[3]);
			return;
		}
		var res = this.svdTransform(
		{
			xx: m[0],
			xy: m[2],
			yx: m[1],
			yy: m[3],
			dx: m[4],
			dy: m[5]
		});
		ctx.rotate(res.angle2);
		ctx.scale(res.sx, res.sy);
		ctx.rotate(res.angle1);
		return;
	},
	brokenSvd: function (m)
	{
		var mt = [m[0], m[2], m[1], m[3], 0, 0];
		var mtm = [mt[0] * m[0] + mt[2] * m[1], mt[1] * m[0] + mt[3] * m[1], mt[0] * m[2] + mt[2] * m[3], mt[1] * m[2] + mt[3] * m[3], 0, 0];
		var a = 1;
		var b = -(mtm[0] + mtm[3]);
		var c = -(mtm[1] * mtm[2]) + (mtm[0] * mtm[3]);
		var d = Math.sqrt(b * b - 4 * a * c);
		var c1 = (-b + d) / (2 * a);
		var c2 = (-b - d) / (2 * a);
		if (c1 < c2) var tmp = c1,
			c1 = c2,
			c2 = tmp;
		var s1 = Math.sqrt(c1);
		var s2 = Math.sqrt(c2);
		var i_s = [1 / s1, 0, 0, 1 / s2, 0, 0];
		var e = ((mtm[0] - c1) / mtm[2]);
		var l = Math.sqrt(1 + e * e);
		var v00 = 1 / l;
		var v10 = e / l;
		var v11 = v00;
		var v01 = -v10;
		var v = [v00, v01, v10, v11, 0, 0];
		var u = m.slice(0);
		this.tMatrixMultiply(u, v);
		this.tMatrixMultiply(u, i_s);
		return [u, [s1, 0, 0, s2, 0, 0], [v00, v10, v01, v11, 0, 0]];
	},
	svdTransform: (function ()
	{
		var m = {};
		m.Matrix2D = function (arg)
		{
			if (arg)
			{
				if (typeof arg == 'number')
				{
					this.xx = this.yy = arg;
				}
				else if (arg instanceof Array)
				{
					if (arg.length > 0)
					{
						var matrix = m.normalize(arg[0]);
						for (var i = 1; i < arg.length; ++i)
						{
							var l = matrix,
								r = m.normalize(arg[i]);
							matrix = new m.Matrix2D();
							matrix.xx = l.xx * r.xx + l.xy * r.yx;
							matrix.xy = l.xx * r.xy + l.xy * r.yy;
							matrix.yx = l.yx * r.xx + l.yy * r.yx;
							matrix.yy = l.yx * r.xy + l.yy * r.yy;
							matrix.dx = l.xx * r.dx + l.xy * r.dy + l.dx;
							matrix.dy = l.yx * r.dx + l.yy * r.dy + l.dy;
						}
						Object.extend(this, matrix);
					}
				}
				else
				{
					Object.extend(this, arg);
				}
			}
		};
		m.normalize = function (matrix)
		{
			return (matrix instanceof m.Matrix2D) ? matrix : new m.Matrix2D(matrix);
		};
		m.multiply = function (matrix)
		{
			var M = m.normalize(matrix);
			for (var i = 1; i < arguments.length; ++i)
			{
				var l = M,
					r = m.normalize(arguments[i]);
				M = new m.Matrix2D();
				M.xx = l.xx * r.xx + l.xy * r.yx;
				M.xy = l.xx * r.xy + l.xy * r.yy;
				M.yx = l.yx * r.xx + l.yy * r.yx;
				M.yy = l.yx * r.xy + l.yy * r.yy;
				M.dx = l.xx * r.dx + l.xy * r.dy + l.dx;
				M.dy = l.yx * r.dx + l.yy * r.dy + l.dy;
			}
			return M;
		}
		m.invert = function (matrix)
		{
			var M = m.normalize(matrix),
				D = M.xx * M.yy - M.xy * M.yx,
				M = new m.Matrix2D(
				{
					xx: M.yy / D,
					xy: -M.xy / D,
					yx: -M.yx / D,
					yy: M.xx / D,
					dx: (M.xy * M.dy - M.yy * M.dx) / D,
					dy: (M.yx * M.dx - M.xx * M.dy) / D
				});
			return M;
		}
		Object.extend(m.Matrix2D, {
			xx: 1,
			xy: 0,
			yx: 0,
			yy: 1,
			dx: 0,
			dy: 0
		});
		var eq = function (a, b)
			{
				return Math.abs(a - b) <= 1e-6 * (Math.abs(a) + Math.abs(b));
			};
		var calcFromValues = function (s1, s2)
			{
				if (!isFinite(s1))
				{
					return s2;
				}
				else if (!isFinite(s2))
				{
					return s1;
				}
				return (s1 + s2) / 2;
			};
		var transpose = function (matrix)
			{
				var M = new m.Matrix2D(matrix);
				return Object.extend(M, {
					dx: 0,
					dy: 0,
					xy: M.yx,
					yx: M.xy
				});
			};
		var scaleSign = function (matrix)
			{
				return (matrix.xx * matrix.yy < 0 || matrix.xy * matrix.yx > 0) ? -1 : 1;
			};
		var eigenvalueDecomposition = function (matrix)
			{
				var M = m.normalize(matrix),
					b = -M.xx - M.yy,
					c = M.xx * M.yy - M.xy * M.yx,
					d = Math.sqrt(b * b - 4 * c),
					l1 = -(b + (b < 0 ? -d : d)) / 2,
					l2 = c / l1,
					vx1 = M.xy / (l1 - M.xx),
					vy1 = 1,
					vx2 = M.xy / (l2 - M.xx),
					vy2 = 1;
				if (eq(l1, l2))
				{
					vx1 = 1, vy1 = 0, vx2 = 0, vy2 = 1;
				}
				if (!isFinite(vx1))
				{
					vx1 = 1, vy1 = (l1 - M.xx) / M.xy;
					if (!isFinite(vy1))
					{
						vx1 = (l1 - M.yy) / M.yx, vy1 = 1;
						if (!isFinite(vx1))
						{
							vx1 = 1, vy1 = M.yx / (l1 - M.yy);
						}
					}
				}
				if (!isFinite(vx2))
				{
					vx2 = 1, vy2 = (l2 - M.xx) / M.xy;
					if (!isFinite(vy2))
					{
						vx2 = (l2 - M.yy) / M.yx, vy2 = 1;
						if (!isFinite(vx2))
						{
							vx2 = 1, vy2 = M.yx / (l2 - M.yy);
						}
					}
				}
				var d1 = Math.sqrt(vx1 * vx1 + vy1 * vy1),
					d2 = Math.sqrt(vx2 * vx2 + vy2 * vy2);
				if (isNaN(vx1 /= d1))
				{
					vx1 = 0;
				}
				if (isNaN(vy1 /= d1))
				{
					vy1 = 0;
				}
				if (isNaN(vx2 /= d2))
				{
					vx2 = 0;
				}
				if (isNaN(vy2 /= d2))
				{
					vy2 = 0;
				}
				return {
					value1: l1,
					value2: l2,
					vector1: {
						x: vx1,
						y: vy1
					},
					vector2: {
						x: vx2,
						y: vy2
					}
				};
			};
		var decomposeSR = function (M, result)
			{
				var sign = scaleSign(M),
					a = result.angle1 = (Math.atan2(M.yx, M.yy) + Math.atan2(-sign * M.xy, sign * M.xx)) / 2,
					cos = Math.cos(a),
					sin = Math.sin(a);
				result.sx = calcFromValues(M.xx / cos, -M.xy / sin);
				result.sy = calcFromValues(M.yy / cos, M.yx / sin);
				return result;
			};
		var decomposeRS = function (M, result)
			{
				var sign = scaleSign(M),
					a = result.angle2 = (Math.atan2(sign * M.yx, sign * M.xx) + Math.atan2(-M.xy, M.yy)) / 2,
					cos = Math.cos(a),
					sin = Math.sin(a);
				result.sx = calcFromValues(M.xx / cos, M.yx / sin);
				result.sy = calcFromValues(M.yy / cos, -M.xy / sin);
				return result;
			};
		return function (matrix)
		{
			var M = m.normalize(matrix),
				result = {
					dx: M.dx,
					dy: M.dy,
					sx: 1,
					sy: 1,
					angle1: 0,
					angle2: 0
				};
			if (eq(M.xy, 0) && eq(M.yx, 0))
			{
				return Object.extend(result, {
					sx: M.xx,
					sy: M.yy
				});
			}
			if (eq(M.xx * M.yx, -M.xy * M.yy))
			{
				return decomposeSR(M, result);
			}
			if (eq(M.xx * M.xy, -M.yx * M.yy))
			{
				return decomposeRS(M, result);
			}
			var MT = transpose(M),
				u = eigenvalueDecomposition([M, MT]),
				v = eigenvalueDecomposition([MT, M]),
				U = new m.Matrix2D(
				{
					xx: u.vector1.x,
					xy: u.vector2.x,
					yx: u.vector1.y,
					yy: u.vector2.y
				}),
				VT = new m.Matrix2D(
				{
					xx: v.vector1.x,
					xy: v.vector1.y,
					yx: v.vector2.x,
					yy: v.vector2.y
				}),
				S = new m.Matrix2D([m.invert(U), M, m.invert(VT)]);
			decomposeSR(VT, result);
			S.xx *= result.sx;
			S.yy *= result.sy;
			decomposeRS(U, result);
			S.xx *= result.sx;
			S.yy *= result.sy;
			return Object.extend(result, {
				sx: S.xx,
				sy: S.yy
			});
		};
	})(),
	setTransform: function (ctx, m, ctm)
	{
		if (ctx.setTransform) return ctx.setTransform.apply(ctx, m);
		this.transform(ctx, this.tInvertMatrix(ctm));
		this.transform(ctx, m);
	},
	skewX: function (ctx, angle)
	{
		return this.transform(ctx, this.tSkewXMatrix(angle));
	},
	skewY: function (ctx, angle)
	{
		return this.transform(ctx, this.tSkewYMatrix(angle));
	},
	tRotate: function (m1, angle)
	{
		var c = Math.cos(angle);
		var s = Math.sin(angle);
		var m11 = m1[0] * c + m1[2] * s;
		var m12 = m1[1] * c + m1[3] * s;
		var m21 = m1[0] * -s + m1[2] * c;
		var m22 = m1[1] * -s + m1[3] * c;
		m1[0] = m11;
		m1[1] = m12;
		m1[2] = m21;
		m1[3] = m22;
		return m1;
	},
	tTranslate: function (m1, x, y)
	{
		m1[4] += m1[0] * x + m1[2] * y;
		m1[5] += m1[1] * x + m1[3] * y;
		return m1;
	},
	tScale: function (m1, sx, sy)
	{
		m1[0] *= sx;
		m1[1] *= sx;
		m1[2] *= sy;
		m1[3] *= sy;
		return m1;
	},
	tSkewX: function (m1, angle)
	{
		return this.tMatrixMultiply(m1, this.tSkewXMatrix(angle));
	},
	tSkewY: function (m1, angle)
	{
		return this.tMatrixMultiply(m1, this.tSkewYMatrix(angle));
	},
	tSkewXMatrix: function (angle)
	{
		return [1, 0, Math.tan(angle), 1, 0, 0];
	},
	tSkewYMatrix: function (angle)
	{
		return [1, Math.tan(angle), 0, 1, 0, 0];
	},
	tRotationMatrix: function (angle)
	{
		var c = Math.cos(angle);
		var s = Math.sin(angle);
		return [c, s, -s, c, 0, 0];
	},
	tTranslationMatrix: function (x, y)
	{
		return [1, 0, 0, 1, x, y];
	},
	tScalingMatrix: function (sx, sy)
	{
		return [sx, 0, 0, sy, 0, 0];
	},
	getTextBackend: function ()
	{
		if (this.textBackend == null) this.textBackend = this.detectTextBackend();
		return this.textBackend;
	},
	detectTextBackend: function ()
	{
		var ctx = this.getTestContext();
		if (ctx.fillText)
		{
			return 'HTML5';
		}
		else if (ctx.mozDrawText)
		{
			return 'MozText';
		}
		else if (ctx.drawString)
		{
			return 'DrawString';
		}
		return 'NONE';
	},
	getSupportsPutImageData: function ()
	{
		if (this.supportsPutImageData == null)
		{
			var ctx = this.getTestContext();
			var support = ctx.putImageData;
			if (support)
			{
				try
				{
					var idata = ctx.getImageData(0, 0, 1, 1);
					idata[0] = 255;
					idata[1] = 0;
					idata[2] = 255;
					idata[3] = 255;
					ctx.putImageData(
					{
						width: 1,
						height: 1,
						data: idata
					}, 0, 0);
					var idata = ctx.getImageData(0, 0, 1, 1);
					support = [255, 0, 255, 255].equals(idata.data);
				}
				catch (e)
				{
					support = false;
				}
			}
			this.supportsPutImageData = support;
		}
		return support;
	},
	getSupportsIsPointInPath: function ()
	{
		if (this.supportsIsPointInPath == null) this.supportsIsPointInPath = !! this.getTestContext().isPointInPath;
		return this.supportsIsPointInPath;
	},
	getIsPointInPathMode: function ()
	{
		if (this.isPointInPathMode == null) this.isPointInPathMode = this.detectIsPointInPathMode();
		return this.isPointInPathMode;
	},
	detectIsPointInPathMode: function ()
	{
		var ctx = this.getTestContext();
		var rv;
		if (!ctx.isPointInPath) return this.USER_SPACE;
		ctx.save();
		ctx.translate(1, 0);
		ctx.beginPath();
		ctx.rect(0, 0, 1, 1);
		if (ctx.isPointInPath(0.3, 0.3))
		{
			rv = this.USER_SPACE;
		}
		else
		{
			rv = this.DEVICE_SPACE;
		}
		ctx.restore();
		return rv;
	},
	isPointInPath: function (ctx, x, y, matrix, callbackObj)
	{
		var rv;
		if (!ctx.isPointInPath)
		{
			if (callbackObj && callbackObj.isPointInPath)
			{
				var xy = this.tMatrixMultiplyPoint(this.tInvertMatrix(matrix), x, y);
				return callbackObj.isPointInPath(xy[0], xy[1]);
			}
			else
			{
				return false;
			}
		}
		else
		{
			if (this.getIsPointInPathMode() == this.USER_SPACE)
			{
				if (!ctx.setTransform)
				{
					var xy = this.tMatrixMultiplyPoint(this.tInvertMatrix(matrix), x, y);
					rv = ctx.isPointInPath(xy[0], xy[1]);
				}
				else
				{
					ctx.save();
					ctx.setTransform(1, 0, 0, 1, 0, 0);
					rv = ctx.isPointInPath(x, y);
					ctx.restore();
				}
			}
			else
			{
				rv = ctx.isPointInPath(x, y);
			}
			return rv;
		}
	}
};
Transformable = cheddar.Class(
{
	needMatrixUpdate: true,
	transform: function (ctx)
	{
		var atm = this.absoluteMatrix;
		var xy = this.x || this.y;
		var rot = this.rotation;
		var sca = this.scale != null;
		var skX = this.skewX;
		var skY = this.skewY;
		var tm = this.matrix;
		var tl = this.transformList;
		if (this.needMatrixUpdate || !this.currentMatrix)
		{
			if (!this.currentMatrix) this.currentMatrix = [1, 0, 0, 1, 0, 0];
			if (this.parent) this.__copyMatrix(this.parent.currentMatrix);
			else this.__identityMatrix();
			if (atm) this.__setMatrixMatrix(this.absoluteMatrix);
			if (xy) this.__translateMatrix(this.x, this.y);
			if (rot) this.__rotateMatrix(this.rotation);
			if (skX) this.__skewXMatrix(this.skewX);
			if (skY) this.__skewYMatrix(this.skewY);
			if (sca) this.__scaleMatrix(this.scale);
			if (tm) this.__matrixMatrix(this.matrix);
			if (tl)
			{
				for (var i = 0; i < this.transformList.length; i++)
				{
					var tl = this.transformList[i];
					this['__' + tl[0] + 'Matrix'](tl[1]);
				}
			}
			this.needMatrixUpdate = false;
		}
		if (!ctx) return;
		this.__setMatrix(ctx, this.currentMatrix);
	},
	distanceTo: function (node)
	{
		return Curves.lineLength([this.x, this.y], [node.x, node.y]);
	},
	angleTo: function (node)
	{
		return Curves.lineAngle([this.x, this.y], [node.x, node.y]);
	},
	setProps: function (obj)
	{
		for (prop in obj)
		{
			this[prop] = obj[prop];
		}
	},
	__setMatrixMatrix: function (matrix)
	{
		if (!this.previousMatrix) this.previousMatrix = [];
		var p = this.previousMatrix;
		var c = this.currentMatrix;
		p[0] = c[0];
		p[1] = c[1];
		p[2] = c[2];
		p[3] = c[3];
		p[4] = c[4];
		p[5] = c[5];
		p = this.currentMatrix;
		c = matrix;
		p[0] = c[0];
		p[1] = c[1];
		p[2] = c[2];
		p[3] = c[3];
		p[4] = c[4];
		p[5] = c[5];
	},
	__copyMatrix: function (matrix)
	{
		var p = this.currentMatrix;
		var c = matrix;
		p[0] = c[0];
		p[1] = c[1];
		p[2] = c[2];
		p[3] = c[3];
		p[4] = c[4];
		p[5] = c[5];
	},
	__identityMatrix: function ()
	{
		var p = this.currentMatrix;
		p[0] = 1;
		p[1] = 0;
		p[2] = 0;
		p[3] = 1;
		p[4] = 0;
		p[5] = 0;
	},
	__translateMatrix: function (x, y)
	{
		if (x.length)
		{
			CanvasSupport.tTranslate(this.currentMatrix, x[0], x[1]);
		}
		else
		{
			CanvasSupport.tTranslate(this.currentMatrix, x, y);
		}
	},
	__rotateMatrix: function (rotation)
	{
		if (rotation.length)
		{
			if (rotation[0] % Math.PI * 2 == 0) return;
			if (rotation[1] || rotation[2])
			{
				CanvasSupport.tTranslate(this.currentMatrix, rotation[1], rotation[2]);
				CanvasSupport.tRotate(this.currentMatrix, rotation[0]);
				CanvasSupport.tTranslate(this.currentMatrix, -rotation[1], -rotation[2]);
			}
			else
			{
				CanvasSupport.tRotate(this.currentMatrix, rotation[0]);
			}
		}
		else
		{
			if (rotation % Math.PI * 2 == 0) return;
			CanvasSupport.tRotate(this.currentMatrix, rotation);
		}
	},
	__skewXMatrix: function (skewX)
	{
		if (skewX.length && skewX[0]) CanvasSupport.tSkewX(this.currentMatrix, skewX[0]);
		else CanvasSupport.tSkewX(this.currentMatrix, skewX);
	},
	__skewYMatrix: function (skewY)
	{
		if (skewY.length && skewY[0]) CanvasSupport.tSkewY(this.currentMatrix, skewY[0]);
		else CanvasSupport.tSkewY(this.currentMatrix, skewY);
	},
	__scaleMatrix: function (scale)
	{
		if (scale.length == 2)
		{
			if (scale[0] == 1 && scale[1] == 1) return;
			CanvasSupport.tScale(this.currentMatrix, scale[0], scale[1]);
		}
		else if (scale.length == 3)
		{
			if (scale[0] == 1 || (scale[0].length && (scale[0][0] == 1 && scale[0][1] == 1))) return;
			CanvasSupport.tTranslate(this.currentMatrix, scale[1], scale[2]);
			if (scale[0].length)
			{
				CanvasSupport.tScale(this.currentMatrix, scale[0][0], scale[0][1]);
			}
			else
			{
				CanvasSupport.tScale(this.currentMatrix, scale[0], scale[0]);
			}
			CanvasSupport.tTranslate(this.currentMatrix, -scale[1], -scale[2]);
		}
		else if (scale != 1)
		{
			CanvasSupport.tScale(this.currentMatrix, scale, scale);
		}
	},
	__matrixMatrix: function (matrix)
	{
		CanvasSupport.tMatrixMultiply(this.currentMatrix, matrix);
	},
	__setMatrix: function (ctx, matrix)
	{
		CanvasSupport.setTransform(ctx, matrix, this.previousMatrix);
	},
	__translate: function (ctx, x, y)
	{
		if (x.length != null) ctx.translate(x[0], x[1]);
		else ctx.translate(x, y);
	},
	__rotate: function (ctx, rotation)
	{
		if (rotation.length)
		{
			if (rotation[1] || rotation[2])
			{
				if (rotation[0] % Math.PI * 2 == 0) return;
				ctx.translate(rotation[1], rotation[2]);
				ctx.rotate(rotation[0]);
				ctx.translate(-rotation[1], -rotation[2]);
			}
			else
			{
				ctx.rotate(rotation[0]);
			}
		}
		else
		{
			ctx.rotate(rotation);
		}
	},
	__skewX: function (ctx, skewX)
	{
		if (skewX.length && skewX[0]) CanvasSupport.skewX(ctx, skewX[0]);
		else CanvasSupport.skewX(ctx, skewX);
	},
	__skewY: function (ctx, skewY)
	{
		if (skewY.length && skewY[0]) CanvasSupport.skewY(ctx, skewY[0]);
		else CanvasSupport.skewY(ctx, skewY);
	},
	__scale: function (ctx, scale)
	{
		if (scale.length == 2)
		{
			ctx.scale(scale[0], scale[1]);
		}
		else if (scale.length == 3)
		{
			ctx.translate(scale[1], scale[2]);
			if (scale[0].length)
			{
				ctx.scale(scale[0][0], scale[0][1]);
			}
			else
			{
				ctx.scale(scale[0], scale[0]);
			}
			ctx.translate(-scale[1], -scale[2]);
		}
		else
		{
			ctx.scale(scale, scale);
		}
	},
	__matrix: function (ctx, matrix)
	{
		CanvasSupport.transform(ctx, matrix);
	}
});
Colors = {
	hsl2rgb: function (h, s, l)
	{
		var r, g, b;
		if (s == 0)
		{
			r = g = b = v;
		}
		else
		{
			var q = (l < 0.5 ? l * (1 + s) : l + s - (l * s));
			var p = 2 * l - q;
			var hk = (h % 360) / 360;
			var tr = hk + 1 / 3;
			var tg = hk;
			var tb = hk - 1 / 3;
			if (tr < 0) tr++;
			if (tr > 1) tr--;
			if (tg < 0) tg++;
			if (tg > 1) tg--;
			if (tb < 0) tb++;
			if (tb > 1) tb--;
			if (tr < 1 / 6) r = p + ((q - p) * 6 * tr);
			else if (tr < 1 / 2) r = q;
			else if (tr < 2 / 3) r = p + ((q - p) * 6 * (2 / 3 - tr));
			else r = p;
			if (tg < 1 / 6) g = p + ((q - p) * 6 * tg);
			else if (tg < 1 / 2) g = q;
			else if (tg < 2 / 3) g = p + ((q - p) * 6 * (2 / 3 - tg));
			else g = p;
			if (tb < 1 / 6) b = p + ((q - p) * 6 * tb);
			else if (tb < 1 / 2) b = q;
			else if (tb < 2 / 3) b = p + ((q - p) * 6 * (2 / 3 - tb));
			else b = p;
		}
		return [r, g, b];
	},
	hsv2rgb: function (h, s, v)
	{
		var r, g, b;
		if (s == 0)
		{
			r = g = b = v;
		}
		else
		{
			h = (h % 360) / 60.0;
			var i = Math.floor(h);
			var f = h - i;
			var p = v * (1 - s);
			var q = v * (1 - s * f);
			var t = v * (1 - s * (1 - f));
			switch (i)
			{
			case 0:
				r = v;
				g = t;
				b = p;
				break;
			case 1:
				r = q;
				g = v;
				b = p;
				break;
			case 2:
				r = p;
				g = v;
				b = t;
				break;
			case 3:
				r = p;
				g = q;
				b = v;
				break;
			case 4:
				r = t;
				g = p;
				b = v;
				break;
			case 5:
				r = v;
				g = p;
				b = q;
				break;
			}
		}
		return [r, g, b];
	},
	parseColorStyle: function (style, ctx)
	{
		if (typeof style == 'string')
		{
			return style;
		}
		else if (style.compiled)
		{
			return style.compiled;
		}
		else if (style.isPattern)
		{
			return style.compile(ctx);
		}
		else if (style.length == 3)
		{
			return 'rgba(' + style.map(Math.round).join(',') + ', 1)';
		}
		else if (style.length == 4)
		{
			return 'rgba(' + Math.round(style[0]) + ',' + Math.round(style[1]) + ',' + Math.round(style[2]) + ',' + style[3] + ')';
		}
		else
		{
			throw ('Bad style: ' + style);
		}
	}
};
if (!window.Mouse) Mouse = {};
Mouse.getRelativeCoords = function (element, event)
{
	var xy = {
		x: 0,
		y: 0
	};
	var osl = 0;
	var ost = 0;
	var el = element;
	while (el)
	{
		osl += el.offsetLeft;
		ost += el.offsetTop;
		el = el.offsetParent;
	}
	xy.x = event.pageX - osl;
	xy.y = event.pageY - ost;
	return xy;
};
Browser = (function ()
{
	var ua = window.navigator.userAgent;
	var khtml = ua.match(/KHTML/);
	var gecko = ua.match(/Gecko/);
	var webkit = ua.match(/WebKit\/\d+/);
	var ie = ua.match(/Explorer/);
	if (khtml) return 'KHTML';
	if (gecko) return 'Gecko';
	if (webkit) return 'Webkit';
	if (ie) return 'IE';
	return 'UNKNOWN';
})();
Mouse.LEFT = 0;
Mouse.MIDDLE = 1;
Mouse.RIGHT = 2;
if (Browser == 'IE')
{
	Mouse.LEFT = 1;
	Mouse.MIDDLE = 4;
}
CanvasNode = cheddar.Class(Transformable, {
	OBJECTBOUNDINGBOX: 'objectBoundingBox',
	visible: true,
	drawable: true,
	display: null,
	visibility: null,
	catchMouse: true,
	pickable: false,
	underCursor: false,
	zIndex: 0,
	x: 0,
	oldX: 0,
	y: 0,
	oldX: 0,
	scale: 1,
	oldScale: 0,
	rotation: 0,
	oldRotation: false,
	matrix: null,
	absoluteMatrix: null,
	transformList: null,
	fill: null,
	stroke: null,
	strokeWidth: null,
	lineCap: null,
	lineJoin: null,
	miterLimit: null,
	absoluteOpacity: null,
	opacity: null,
	oldOpacity: null,
	fillOpacity: null,
	strokeOpacity: null,
	compositeOperation: null,
	shadowColor: null,
	shadowBlur: null,
	shadowOffsetX: null,
	shadowOffsetY: null,
	font: null,
	textAlign: null,
	textBaseline: null,
	cursor: null,
	changed: true,
	tagName: 'g',
	getNextSibling: function ()
	{
		if (this.parentNode) return this.parentNode.childNodes[this.parentNode.childNodes.indexOf(this) + 1];
		return null;
	},
	getPreviousSibling: function ()
	{
		if (this.parentNode) return this.parentNode.childNodes[this.parentNode.childNodes.indexOf(this) - 1];
		return null;
	},
	initialize: function (config)
	{
		this.root = this;
		this.currentMatrix = [1, 0, 0, 1, 0, 0];
		this.previousMatrix = [1, 0, 0, 1, 0, 0];
		this.needMatrixUpdate = true;
		this.childNodes = [];
		this.frameListeners = [];
		this.eventListeners = {};
		if (config) Object.extend(this, config);
	},
	clone: function ()
	{
		var c = Object.clone(this);
		c.parent = c.root = null;
		for (var i in this)
		{
			if (typeof (this[i]) == 'object') c[i] = Object.clone(this[i]);
		}
		c.parent = c.root = null;
		c.childNodes = [];
		c.setRoot(null);
		for (var i = 0; i < this.childNodes.length; i++)
		{
			var ch = this.childNodes[i].clone();
			c.append(ch);
		}
		return c;
	},
	cloneNode: function ()
	{
		return this.clone()
	},
	getElementById: function (id)
	{
		if (this.id == id) return this;
		for (var i = 0; i < this.childNodes.length; i++)
		{
			var n = this.childNodes[i].getElementById(id);
			if (n) return n;
		}
		return null;
	},
	$: function (id)
	{
		return this.getElementById(id);
	},
	appendChild: function ()
	{
		return this.append.apply(this, arguments);
	},
	append: function (obj)
	{
		var a = $A(arguments);
		if (obj.length > 0) a = obj;
		for (var i = 0; i < a.length; i++)
		{
			if (a[i].parent) a[i].removeSelf();
			this.childNodes.push(a[i]);
			a[i].parent = a[i].parentNode = this;
			a[i].setRoot(this.root);
		}
		this.changed = true;
	},
	removeAllChildren: function ()
	{
		this.remove.apply(this, this.childNodes);
	},
	removeChild: function ()
	{
		return this.remove.apply(this, arguments);
	},
	remove: function (obj)
	{
		var a = arguments;
		if (obj.length > 0) a = obj;
		for (var i = 0; i < a.length; i++)
		{
			this.childNodes.deleteFirst(a[i])
			delete a[i].parent
			delete a[i].parentNode;
			a[i].setRoot(null);
		}
		this.changed = true;
	},
	removeSelf: function ()
	{
		if (this.parentNode)
		{
			this.parentNode.remove(this);
		}
	},
	contains: function (obj)
	{
		while (obj)
		{
			if (obj == this) return true;
			obj = obj.parentNode;
		}
		return false;
	},
	setRoot: function (root)
	{
		if (!root) root = this;
		this.dispatchEvent(
		{
			type: 'rootChanged',
			canvasTarget: this,
			relatedTarget: root
		});
		this.root = root;
		for (var i = 0; i < this.childNodes.length; i++)
		this.childNodes[i].setRoot(root);
	},
	addFrameListener: function (f)
	{
		this.frameListeners.push(f);
	},
	removeFrameListener: function (f)
	{
		this.frameListeners.deleteFirst(f);
	},
	addEventListener: function (type, listener, capture)
	{
		if (!this.eventListeners[type]) this.eventListeners[type] = {
			capture: [],
			bubble: []
		};
		this.eventListeners[type][capture ? 'capture' : 'bubble'].push(listener);
	},
	when: function (type, listener, capture)
	{
		this.addEventListener(type, listener, capture || false);
	},
	removeEventListener: function (type, listener, capture)
	{
		if (!this.eventListeners[type]) return;
		this.eventListeners[type][capture ? 'capture' : 'bubble'].deleteFirst(listener);
		if (this.eventListeners[type].capture.length == 0 && this.eventListeners[type].bubble.length == 0) delete this.eventListeners[type];
	},
	dispatchEvent: function (event)
	{
		var type = event.type;
		if (!event.canvasTarget)
		{
			if (type.search(/^(key|text)/i) == 0)
			{
				event.canvasTarget = this.root.focused || this.root.target;
			}
			else
			{
				event.canvasTarget = this.root.target;
			}
			if (!event.canvasTarget) event.canvasTarget = this;
		}
		var path = [];
		var obj = event.canvasTarget;
		while (obj && obj != this)
		{
			path.push(obj);
			obj = obj.parent;
		}
		path.push(this);
		event.canvasPhase = 'capture';
		for (var i = path.length - 1; i >= 0; i--)
		if (!path[i].handleEvent(event)) return false;
		event.canvasPhase = 'bubble';
		for (var i = 0; i < path.length; i++)
		if (!path[i].handleEvent(event)) return false;
		return true;
	},
	broadcastEvent: function (event)
	{
		var type = event.type;
		event.canvasPhase = 'capture';
		if (!this.handleEvent(event)) return false;
		for (var i = 0; i < this.childNodes.length; i++)
		if (!this.childNodes[i].broadcastEvent(event)) return false;
		event.canvasPhase = 'bubble';
		if (!this.handleEvent(event)) return false;
		return true;
	},
	handleEvent: function (event)
	{
		var type = event.type;
		var phase = event.canvasPhase;
		if (this.cursor && phase == 'capture') event.cursor = this.cursor;
		var els = this.eventListeners[type];
		els = els && els[phase];
		if (els)
		{
			for (var i = 0; i < els.length; i++)
			{
				var rv = els[i].call(this, event);
				if (rv == false || event.stopped)
				{
					if (!event.stopped) event.stopPropagation();
					event.stopped = true;
					return false;
				}
			}
		}
		return true;
	},
	handleUpdate: function (time, timeDelta)
	{
		this.update(time, timeDelta);
		if (this.x != this.oldX || this.y != this.oldY || this.opacity != this.oldOpacity || this.rotation != this.oldRotation)
		{
			this.changed = true;
		}
		this.oldX = this.x;
		this.oldY = this.y;
		this.oldOpacity = this.opacity;
		this.oldRotation = this.rotation;
		this.willBeDrawn = (!this.parent || this.parent.willBeDrawn) && (this.display ? this.display != 'none' : this.visible);
		for (var i = 0; i < this.childNodes.length; i++)
		this.childNodes[i].handleUpdate(time, timeDelta);
		if (this.parent && this.changed)
		{
			this.parent.changed = this.changed;
			this.changed = false;
		}
		this.needMatrixUpdate = true;
	},
	update: function (time, timeDelta)
	{
		var fl = this.frameListeners.slice(0);
		for (var i = 0; i < fl.length; i++)
		{
			if (this.frameListeners.includes(fl[i])) fl[i].apply(this, arguments);
		}
	},
	handlePick: function (ctx)
	{
		if (this.display) this.visible = (this.display != 'none');
		if (this.visibility) this.drawable = (this.visibility != 'hidden');
		this.underCursor = false;
		if (this.visible && this.catchMouse && this.root.absoluteMouseX != null)
		{
			ctx.save();
			this.transform(ctx, true);
			if (this.pickable && this.drawable)
			{
				if (ctx.isPointInPath)
				{
					ctx.beginPath();
					if (this.drawPickingPath) this.drawPickingPath(ctx);
				}
				this.underCursor = CanvasSupport.isPointInPath(this.drawPickingPath ? ctx : false, this.root.mouseX, this.root.mouseY, this.currentMatrix, this);
				if (this.underCursor) this.root.target = this;
			}
			else
			{
				this.underCursor = false;
			}
			var c = this.__getChildrenCopy();
			this.__zSort(c);
			for (var i = 0; i < c.length; i++)
			{
				c[i].handlePick(ctx);
				if (!this.underCursor) this.underCursor = c[i].underCursor;
			}
			ctx.restore();
		}
		else
		{
			var c = this.__getChildrenCopy();
			while (c.length > 0)
			{
				var c0 = c.pop();
				if (c0.underCursor)
				{
					c0.underCursor = false;
					Array.prototype.push.apply(c, c0.childNodes);
				}
			}
		}
	},
	__zSort: function (c)
	{
		c.stableSort(function (c1, c2)
		{
			return c1.zIndex - c2.zIndex;
		});
	},
	__getChildrenCopy: function ()
	{
		if (this.__childNodesCopy)
		{
			while (this.__childNodesCopy.length > this.childNodes.length)
			this.__childNodesCopy.pop();
			for (var i = 0; i < this.childNodes.length; i++)
			this.__childNodesCopy[i] = this.childNodes[i];
		}
		else
		{
			this.__childNodesCopy = this.childNodes.slice(0);
		}
		return this.__childNodesCopy;
	},
	isPointInPath: false,
	handleDraw: function (ctx)
	{
		if (this.display) this.visible = (this.display != 'none');
		if (this.visibility) this.drawable = (this.visibility != 'hidden');
		if (!this.visible || this.opacity == 0) return;
		ctx.save();
		var pff = ctx.fontFamily;
		var pfs = ctx.fontSize;
		var pfo = ctx.fillOn;
		var pso = ctx.strokeOn;
		if (this.fontFamily) ctx.fontFamily = this.fontFamily;
		if (this.fontSize) ctx.fontSize = this.fontSize;
		this.transform(ctx);
		if (this.clipPath)
		{
			ctx.beginPath();
			if (this.clipPath.units == this.OBJECTBOUNDINGBOX)
			{
				var bb = this.getSubtreeBoundingBox(true);
				ctx.save();
				ctx.translate(bb[0], bb[1]);
				ctx.scale(bb[2], bb[3]);
				this.clipPath.createSubtreePath(ctx, true);
				ctx.restore();
				ctx.clip();
			}
			else
			{
				this.clipPath.createSubtreePath(ctx, true);
				ctx.clip();
			}
		}
		if (this.drawable && this.draw) this.draw(ctx);
		var c = this.__getChildrenCopy();
		this.__zSort(c);
		for (var i = 0; i < c.length; i++)
		{
			c[i].handleDraw(ctx);
		}
		ctx.fontFamily = pff;
		ctx.fontSize = pfs;
		ctx.fillOn = pfo;
		ctx.strokeOn = pso;
		ctx.restore();
	},
	transform: function (ctx, onlyTransform)
	{
		Transformable.prototype.transform.call(this, ctx);
		if (onlyTransform) return;
		if (this.fill != null)
		{
			if (!this.fill || this.fill == 'none')
			{
				ctx.fillOn = false;
			}
			else
			{
				ctx.fillOn = true;
				if (this.fill != true)
				{
					var fillStyle = Colors.parseColorStyle(this.fill, ctx);
					ctx.setFillStyle(fillStyle);
				}
			}
		}
		if (this.stroke != null)
		{
			if (!this.stroke || this.stroke == 'none')
			{
				ctx.strokeOn = false;
			}
			else
			{
				ctx.strokeOn = true;
				if (this.stroke != true) ctx.setStrokeStyle(Colors.parseColorStyle(this.stroke, ctx));
			}
		}
		if (this.strokeWidth != null) ctx.setLineWidth(this.strokeWidth);
		if (this.lineCap != null) ctx.setLineCap(this.lineCap);
		if (this.lineJoin != null) ctx.setLineJoin(this.lineJoin);
		if (this.miterLimit != null) ctx.setMiterLimit(this.miterLimit);
		if (this.absoluteOpacity != null) ctx.setGlobalAlpha(this.absoluteOpacity);
		if (this.opacity != null) ctx.setGlobalAlpha(ctx.globalAlpha * this.opacity);
		if (this.compositeOperation != null) ctx.setGlobalCompositeOperation(this.compositeOperation);
		if (this.shadowColor != null) ctx.setShadowColor(Colors.parseColorStyle(this.shadowColor, ctx));
		if (this.shadowBlur != null) ctx.setShadowBlur(this.shadowBlur);
		if (this.shadowOffsetX != null) ctx.setShadowOffsetX(this.shadowOffsetX);
		if (this.shadowOffsetY != null) ctx.setShadowOffsetY(this.shadowOffsetY);
		if (this.textAlign != null) ctx.setTextAlign(this.textAlign);
		if (this.textBaseline != null) ctx.setTextBaseline(this.textBaseline);
		if (this.font != null) ctx.setFont(this.font);
	},
	drawPickingPath: false,
	draw: false,
	createSubtreePath: function (ctx, skipTransform)
	{
		ctx.save();
		if (!skipTransform) this.transform(ctx, true);
		for (var i = 0; i < this.childNodes.length; i++)
		this.childNodes[i].createSubtreePath(ctx);
		ctx.restore();
	},
	getSubtreeBoundingBox: function (identity)
	{
		if (identity)
		{
			var p = this.parent;
			this.parent = null;
			this.needMatrixUpdate = true;
		}
		var bb = this.getAxisAlignedBoundingBox();
		for (var i = 0; i < this.childNodes.length; i++)
		{
			var cbb = this.childNodes[i].getSubtreeBoundingBox();
			if (!bb)
			{
				bb = cbb;
			}
			else if (cbb)
			{
				this.mergeBoundingBoxes(bb, cbb);
			}
		}
		if (identity)
		{
			this.parent = p;
			this.needMatrixUpdate = true;
		}
		return bb;
	},
	mergeBoundingBoxes: function (bb, bb2)
	{
		var obx = bb[0],
			oby = bb[1];
		if (bb[0] > bb2[0]) bb[0] = bb2[0];
		if (bb[1] > bb2[1]) bb[1] = bb2[1];
		bb[2] = bb[2] + obx - bb[0];
		bb[3] = bb[3] + oby - bb[1];
		if (bb[2] + bb[0] < bb2[2] + bb2[0]) bb[2] = bb2[2] + bb2[0] - bb[0];
		if (bb[3] + bb[1] < bb2[3] + bb2[1]) bb[3] = bb2[3] + bb2[1] - bb[1];
	},
	getAxisAlignedBoundingBox: function ()
	{
		this.transform(null, true);
		if (!this.getBoundingBox) return null;
		var bbox = this.getBoundingBox();
		var xy1 = CanvasSupport.tMatrixMultiplyPoint(this.currentMatrix, bbox[0], bbox[1]);
		var xy2 = CanvasSupport.tMatrixMultiplyPoint(this.currentMatrix, bbox[0] + bbox[2], bbox[1] + bbox[3]);
		var xy3 = CanvasSupport.tMatrixMultiplyPoint(this.currentMatrix, bbox[0], bbox[1] + bbox[3]);
		var xy4 = CanvasSupport.tMatrixMultiplyPoint(this.currentMatrix, bbox[0] + bbox[2], bbox[1]);
		var x1 = Math.min(xy1[0], xy2[0], xy3[0], xy4[0]);
		var x2 = Math.max(xy1[0], xy2[0], xy3[0], xy4[0]);
		var y1 = Math.min(xy1[1], xy2[1], xy3[1], xy4[1]);
		var y2 = Math.max(xy1[1], xy2[1], xy3[1], xy4[1]);
		return [x1, y1, x2 - x1, y2 - y1];
	},
	makeDraggable: function ()
	{
		this.addEventListener('dragstart', function (ev)
		{
			this.dragStartPosition = {
				x: this.x,
				y: this.y
			};
			ev.stopPropagation();
			ev.preventDefault();
			return false;
		}, false);
		this.addEventListener('drag', function (ev)
		{
			this.x = this.dragStartPosition.x + this.root.dragX / this.parent.currentMatrix[0];
			this.y = this.dragStartPosition.y + this.root.dragY / this.parent.currentMatrix[3];
			ev.stopPropagation();
			ev.preventDefault();
			return false;
		}, false);
	}
});
Canvas = cheddar.Class(CanvasNode, {
	clear: true,
	frameLoop: false,
	recording: false,
	opacity: 1,
	frame: 0,
	elapsed: 0,
	frameDuration: 30,
	speed: 1.0,
	time: 0,
	fps: 0,
	currentRealFps: 0,
	currentFps: 0,
	fpsFrames: 30,
	startTime: 0,
	realFps: 0,
	fixedTimestep: false,
	playOnlyWhenFocused: false,
	isPlaying: true,
	redrawOnlyWhenChanged: false,
	changed: true,
	drawBoundingBoxes: false,
	cursor: 'default',
	mouseDown: false,
	mouseEvents: [],
	absoluteMouseX: null,
	absoluteMouseY: null,
	mouseX: null,
	mouseY: null,
	elementNodeZIndexCounter: 0,
	initialize: function (canvas, config)
	{
		if (arguments.length > 2)
		{
			var container = arguments[0]
			var w = arguments[1]
			var h = arguments[2]
			var config = arguments[3]
			var canvas = E.canvas(w, h)
			var canvasContainer = E('div', canvas, {
				style: {
					overflow: 'hidden',
					width: w + 'px',
					height: h + 'px',
					position: 'relative'
				}
			})
			this.canvasContainer = canvasContainer
			if (container) container.appendChild(canvasContainer)
		}
		CanvasNode.initialize.call(this, config)
		this.mouseEventStack = []
		this.canvas = canvas
		canvas.canvas = this
		this.width = this.canvas.width
		this.height = this.canvas.height
		var th = this
		this.frameHandler = function ()
			{
				th.onFrame()
			}
		this.canvas.addEventListener('DOMNodeInserted', function (ev)
		{
			if (ev.target == this) th.addEventListeners()
		}, false)
		this.canvas.addEventListener('DOMNodeRemoved', function (ev)
		{
			if (ev.target == this) th.removeEventListeners()
		}, false)
		if (this.canvas.parentNode) this.addEventListeners()
		this.startTime = new Date().getTime()
		if (this.isPlaying) this.play()
	},
	removeEventListeners: function ()
	{},
	addEventListeners: function ()
	{
		var th = this
		this.canvas.parentNode.addMouseEvent = function (e)
			{
				var xy = Mouse.getRelativeCoords(this, e)
				th.absoluteMouseX = xy.x
				th.absoluteMouseY = xy.y
				var style = document.defaultView.getComputedStyle(th.canvas, "")
				var w = parseFloat(style.getPropertyValue('width'))
				var h = parseFloat(style.getPropertyValue('height'))
				th.mouseX = th.absoluteMouseX * (w / th.canvas.width)
				th.mouseY = th.absoluteMouseY * (h / th.canvas.height)
				th.addMouseEvent(th.mouseX, th.mouseY, th.mouseDown)
			}
		this.canvas.parentNode.contains = this.contains
		this.canvas.parentNode.addEventListener('mousedown', function (e)
		{
			th.mouseDown = true
			if (th.keyTarget != th.target)
			{
				if (th.keyTarget) th.dispatchEvent(
				{
					type: 'blur',
					canvasTarget: th.keyTarget
				})
				th.keyTarget = th.target
				if (th.keyTarget) th.dispatchEvent(
				{
					type: 'focus',
					canvasTarget: th.keyTarget
				})
			}
			this.addMouseEvent(e)
		}, true)
		this.canvas.parentNode.addEventListener('mouseup', function (e)
		{
			this.addMouseEvent(e)
			th.mouseDown = false
		}, true)
		this.canvas.parentNode.addEventListener('mousemove', function (e)
		{
			this.addMouseEvent(e)
			if (th.prevClientX == null)
			{
				th.prevClientX = e.clientX
				th.prevClientY = e.clientY
			}
			if (th.dragTarget)
			{
				var nev = document.createEvent('MouseEvents')
				nev.initMouseEvent('drag', true, true, window, e.detail, e.screenX, e.screenY, e.clientX, e.clientY, e.ctrlKey, e.altKey, e.shiftKey, e.metaKey, e.button, e.relatedTarget)
				nev.canvasTarget = th.dragTarget
				nev.dx = e.clientX - th.prevClientX
				nev.dy = e.clientY - th.prevClientY
				th.dragX += nev.dx
				th.dragY += nev.dy
				th.dispatchEvent(nev)
			}
			if (!th.mouseDown)
			{
				if (th.dragTarget)
				{
					var nev = document.createEvent('MouseEvents')
					nev.initMouseEvent('dragend', true, true, window, e.detail, e.screenX, e.screenY, e.clientX, e.clientY, e.ctrlKey, e.altKey, e.shiftKey, e.metaKey, e.button, e.relatedTarget)
					nev.canvasTarget = th.dragTarget
					th.dispatchEvent(nev)
					th.dragX = th.dragY = 0
					th.dragTarget = false
				}
			}
			else if (!th.dragTarget && th.target)
			{
				th.dragTarget = th.target
				var nev = document.createEvent('MouseEvents')
				nev.initMouseEvent('dragstart', true, true, window, e.detail, e.screenX, e.screenY, e.clientX, e.clientY, e.ctrlKey, e.altKey, e.shiftKey, e.metaKey, e.button, e.relatedTarget)
				nev.canvasTarget = th.dragTarget
				th.dragStartX = e.clientX
				th.dragStartY = e.clientY
				th.dragX = th.dragY = 0
				th.dispatchEvent(nev)
			}
			th.prevClientX = e.clientX
			th.prevClientY = e.clientY
		}, true)
		this.canvas.parentNode.addEventListener('mouseout', function (e)
		{
			if (!CanvasNode.contains.call(this, e.relatedTarget)) th.absoluteMouseX = th.absoluteMouseY = th.mouseX = th.mouseY = null
		}, true)
		var dispatch = this.dispatchEvent.bind(this)
		var types = ['mousemove', 'mouseover', 'mouseout', 'click', 'dblclick', 'mousedown', 'mouseup', 'keypress', 'keydown', 'keyup', 'DOMMouseScroll', 'mousewheel', 'mousemultiwheel', 'textInput', 'focus', 'blur']
		for (var i = 0; i < types.length; i++)
		{
			this.canvas.parentNode.addEventListener(types[i], dispatch, false)
		}
		this.keys = {}
		this.windowEventListeners = {
			keydown: function (ev)
			{
				if (th.keyTarget)
				{
					th.updateKeys(ev)
					ev.canvasTarget = th.keyTarget
					th.dispatchEvent(ev)
				}
			},
			keyup: function (ev)
			{
				if (th.keyTarget)
				{
					th.updateKeys(ev)
					ev.canvasTarget = th.keyTarget
					th.dispatchEvent(ev)
				}
			},
			keypress: function (ev)
			{
				if (th.keyTarget)
				{
					ev.canvasTarget = th.keyTarget
					th.dispatchEvent(ev)
				}
			},
			blur: function (ev)
			{
				th.absoluteMouseX = th.absoluteMouseY = null
				if (th.playOnlyWhenFocused && th.isPlaying)
				{
					th.stop()
					th.__blurStop = true
				}
			},
			focus: function (ev)
			{
				if (th.__blurStop && !th.isPlaying) th.play()
			},
			mouseup: function (e)
			{
				th.mouseDown = false
				if (th.dragTarget)
				{
					var nev = document.createEvent('MouseEvents')
					nev.initMouseEvent('dragend', true, true, window, e.detail, e.screenX, e.screenY, e.clientX, e.clientY, e.ctrlKey, e.altKey, e.shiftKey, e.metaKey, e.button, e.relatedTarget)
					nev.canvasTarget = th.dragTarget
					th.dispatchEvent(nev)
					th.dragTarget = false
				}
				if (!th.canvas.parentNode.contains(e.target))
				{
					var rv = th.dispatchEvent(e)
					if (th.keyTarget)
					{
						th.dispatchEvent(
						{
							type: 'blur',
							canvasTarget: th.keyTarget
						})
						th.keyTarget = null
					}
					return rv
				}
			},
			mousemove: function (ev)
			{
				if (th.__blurStop && !th.isPlaying) th.play()
				if (!th.canvas.parentNode.contains(ev.target) && th.mouseDown) return th.dispatchEvent(ev)
			}
		}
		this.canvas.parentNode.addEventListener('DOMNodeRemoved', function (ev)
		{
			if (ev.target == this) th.removeWindowEventListeners()
		}, false)
		this.canvas.parentNode.addEventListener('DOMNodeInserted', function (ev)
		{
			if (ev.target == this) th.addWindowEventListeners()
		}, false)
		if (this.canvas.parentNode.parentNode) this.addWindowEventListeners()
	},
	updateKeys: function (ev)
	{
		this.keys.shift = ev.shiftKey
		this.keys.ctrl = ev.ctrlKey
		this.keys.alt = ev.altKey
		this.keys.meta = ev.metaKey
		var state = (ev.type == 'keydown')
		switch (ev.keyCode)
		{
		case 37:
			this.keys.left = state;
			break
		case 38:
			this.keys.up = state;
			break
		case 39:
			this.keys.right = state;
			break
		case 40:
			this.keys.down = state;
			break
		case 32:
			this.keys.space = state;
			break
		case 13:
			this.keys.enter = state;
			break
		case 9:
			this.keys.tab = state;
			break
		case 8:
			this.keys.backspace = state;
			break
		case 16:
			this.keys.shift = state;
			break
		case 17:
			this.keys.ctrl = state;
			break
		case 18:
			this.keys.alt = state;
			break
		}
		this.keys[ev.keyCode] = state
	},
	addWindowEventListeners: function ()
	{
		for (var i in this.windowEventListeners)
		window.addEventListener(i, this.windowEventListeners[i], false)
	},
	removeWindowEventListeners: function ()
	{
		for (var i in this.windowEventListeners)
		window.removeEventListener(i, this.windowEventListeners[i], false)
	},
	addMouseEvent: function (x, y, mouseDown)
	{
		var a = this.allocMouseEvent()
		a[0] = x
		a[1] = y
		a[2] = mouseDown
		this.mouseEvents.push(a)
	},
	allocMouseEvent: function ()
	{
		if (this.mouseEventStack.length > 0)
		{
			return this.mouseEventStack.pop()
		}
		else
		{
			return [null, null, null]
		}
	},
	freeMouseEvent: function (ev)
	{
		this.mouseEventStack.push(ev)
		if (this.mouseEventStack.length > 100) this.mouseEventStack.splice(0, this.mouseEventStack.length)
	},
	clearMouseEvents: function ()
	{
		while (this.mouseEvents.length > 0)
		this.freeMouseEvent(this.mouseEvents.pop())
	},
	createFrameLoop: function ()
	{
		var self = this;
		var fl = {
			running: true,
			stop: function ()
			{
				this.running = false;
			},
			run: function ()
			{
				if (fl.running)
				{
					self.onFrame();
					requestAnimFrame(fl.run, self.canvas);
				}
			}
		};
		requestAnimFrame(fl.run, this.canvas);
		return fl;
	},
	play: function ()
	{
		this.stop();
		this.realTime = new Date().getTime();
		this.frameLoop = this.createFrameLoop();
		this.isPlaying = true;
	},
	stop: function ()
	{
		this.__blurStop = false;
		if (this.frameLoop)
		{
			this.frameLoop.stop();
			this.frameLoop = null;
		}
		this.isPlaying = false;
	},
	dispatchEvent: function (ev)
	{
		var rv = CanvasNode.prototype.dispatchEvent.call(this, ev)
		if (ev.cursor)
		{
			if (this.canvas.style.cursor != ev.cursor) this.canvas.style.cursor = ev.cursor
		}
		else
		{
			if (this.canvas.style.cursor != this.cursor) this.canvas.style.cursor = this.cursor
		}
		return rv
	},
	onFrame: function (time, timeDelta)
	{
		this.elementNodeZIndexCounter = 0
		var ctx = this.getContext()
		try
		{
			var realTime = new Date().getTime()
			this.currentRealElapsed = (realTime - this.realTime)
			this.currentRealFps = 1000 / this.currentRealElapsed
			var dt = this.frameDuration * this.speed
			if (!this.fixedTimestep) dt = this.currentRealElapsed * this.speed
			this.realTime = realTime
			if (time != null)
			{
				this.time = time
				if (timeDelta) dt = timeDelta
			}
			else
			{
				this.time += dt
			}
			this.previousTarget = this.target
			this.target = null
			if (this.catchMouse) this.handlePick(ctx)
			if (this.previousTarget != this.target)
			{
				if (this.previousTarget)
				{
					var nev = document.createEvent('MouseEvents')
					nev.initMouseEvent('mouseout', true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null)
					nev.canvasTarget = this.previousTarget
					this.dispatchEvent(nev)
				}
				if (this.target)
				{
					var nev = document.createEvent('MouseEvents')
					nev.initMouseEvent('mouseover', true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null)
					nev.canvasTarget = this.target
					this.dispatchEvent(nev)
				}
			}
			this.handleUpdate(this.time, dt)
			this.clearMouseEvents()
			if (!this.redrawOnlyWhenChanged || this.changed)
			{
				try
				{
					this.handleDraw(ctx)
				}
				catch (e)
				{
					console.log(e)
					throw (e)
				}
				this.changed = false
			}
			this.currentElapsed = (new Date().getTime() - this.realTime)
			this.elapsed += this.currentElapsed
			this.currentFps = 1000 / this.currentElapsed
			this.frame++
			if (this.frame % this.fpsFrames == 0)
			{
				this.fps = this.fpsFrames * 1000 / (this.elapsed)
				this.realFps = this.fpsFrames * 1000 / (new Date().getTime() - this.startTime)
				this.elapsed = 0
				this.startTime = new Date().getTime()
			}
		}
		catch (e)
		{
			if (ctx)
			{
				try
				{
					for (var i = 0; i < 1000; i++)
					ctx.restore()
				}
				catch (er)
				{}
			}
			delete this.context
			throw (e)
		}
	},
	getContext: function ()
	{
		if (this.recording) return this.getRecordingContext()
		else if (this.useMockContext) return this.getMockContext()
		else return this.get2DContext()
	},
	get2DContext: function ()
	{
		if (!this.context)
		{
			var ctx = CanvasSupport.getContext(this.canvas, '2d')
			this.context = ctx
		}
		return this.context
	},
	getMockContext: function ()
	{
		if (!this.fakeContext)
		{
			var ctx = this.get2DContext()
			this.fakeContext = {}
			var f = function ()
				{
					return this
				}
			for (var i in ctx)
			{
				if (typeof (ctx[i]) == 'function') this.fakeContext[i] = f
				else this.fakeContext[i] = ctx[i]
			}
			this.fakeContext.isMockObject = true
			this.fakeContext.addColorStop = f
		}
		return this.fakeContext
	},
	getRecordingContext: function ()
	{
		if (!this.recordingContext) this.recordingContext = new RecordingContext()
		return this.recordingContext
	},
	drawPickingPath: function (ctx)
	{
		ctx.rect(0, 0, this.canvas.width, this.canvas.height)
	},
	isPointInPath: function (x, y)
	{
		return ((x >= 0) && (x <= this.canvas.width) && (y >= 0) && (y <= this.canvas.height))
	},
	draw: function (ctx)
	{
		ctx.setGlobalAlpha(this.opacity)
		if (this.clear)
		{
			if (ctx.fillOn)
			{
				ctx.beginPath()
				ctx.rect(0, 0, this.canvas.width, this.canvas.height)
				ctx.fill()
			}
			else
			{
				ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
			}
		}
		ctx.fillStyle = 'black'
		ctx.strokeStyle = 'black'
		ctx.fillOn = false
		ctx.strokeOn = false
	}
})
cheddar.tweener = {
	looping: false,
	frameRate: 60,
	objects: [],
	defaultOptions: {
		time: 1,
		transition: 'easeoutexpo',
		delay: 0,
		prefix: {},
		suffix: {},
		onStart: undefined,
		onStartParams: undefined,
		onUpdate: undefined,
		onUpdateParams: undefined,
		onComplete: undefined,
		onCompleteParams: undefined
	},
	inited: false,
	easingFunctionsLowerCase: {},
	init: function ()
	{
		this.inited = true;
		for (var key in cheddar.tweener.easingFunctions)
		{
			this.easingFunctionsLowerCase[key.toLowerCase()] = cheddar.tweener.easingFunctions[key];
		}
	},
	toNumber: function (value, prefix, suffix)
	{
		if (!suffix) suffix = 'px';
		return value.toString().match(/[0-9]/) ? Number(value.toString().replace(new RegExp(suffix + '$'), '').replace(new RegExp('^' + (prefix ? prefix : '')), '')) : 0;
	},
	addTween: function (obj, options)
	{
		var self = this;
		if (!this.inited) this.init();
		var o = {};
		o.target = obj;
		o.targetPropeties = {};
		for (var key in this.defaultOptions)
		{
			if (typeof options[key] != 'undefined')
			{
				o[key] = options[key];
				delete options[key];
			}
			else
			{
				o[key] = this.defaultOptions[key];
			}
		}
		if (typeof o.transition == 'function')
		{
			o.easing = o.transition;
		}
		else
		{
			o.easing = this.easingFunctionsLowerCase[o.transition.toLowerCase()];
		}
		for (var key in options)
		{
			if (!o.prefix[key]) o.prefix[key] = '';
			if (!o.suffix[key]) o.suffix[key] = '';
			var sB = this.toNumber(obj[key], o.prefix[key], o.suffix[key]);
			o.targetPropeties[key] = {
				b: sB,
				c: options[key] - sB
			};
		}
		setTimeout(function ()
		{
			o.startTime = (new Date() - 0);
			o.endTime = o.time * 1000 + o.startTime;
			if (typeof o.onStart == 'function')
			{
				if (o.onStartParams)
				{
					o.onStart.apply(o, o.onStartParams);
				}
				else
				{
					o.onStart();
				}
			}
			self.objects.push(o);
			if (!self.looping)
			{
				self.looping = true;
				self.eventLoop.call(self);
			}
		}, o.delay * 1000);
		return o;
	},
	eventLoop: function ()
	{
		var now = (new Date() - 0);
		for (var i = 0; i < this.objects.length; i++)
		{
			var o = this.objects[i];
			var t = now - o.startTime;
			var d = o.endTime - o.startTime;
			if (t >= d)
			{
				for (var property in o.targetPropeties)
				{
					var tP = o.targetPropeties[property];
					try
					{
						o.target[property] = (tP.b + tP.c);
					}
					catch (e)
					{}
				}
				this.objects.splice(i, 1);
				if (typeof o.onUpdate == 'function')
				{
					if (o.onUpdateParams)
					{
						o.onUpdate.apply(o, o.onUpdateParams);
					}
					else
					{
						o.onUpdate();
					}
				}
				if (typeof o.onComplete == 'function')
				{
					if (o.onCompleteParams)
					{
						o.onComplete.apply(o, o.onCompleteParams);
					}
					else
					{
						o.onComplete();
					}
				}
			}
			else
			{
				for (var property in o.targetPropeties)
				{
					var tP = o.targetPropeties[property];
					var val = o.easing(t, tP.b, tP.c, d);
					try
					{
						o.target[property] = val;
					}
					catch (e)
					{}
				}
				if (typeof o.onUpdate == 'function')
				{
					if (o.onUpdateParams)
					{
						o.onUpdate.apply(o, o.onUpdateParams);
					}
					else
					{
						o.onUpdate();
					}
				}
			}
		}
		if (this.objects.length > 0)
		{
			var self = this;
			setTimeout(function ()
			{
				self.eventLoop()
			}, 1000 / self.frameRate);
		}
		else
		{
			this.looping = false;
		}
	},
	removeTweensOf: function (targ)
	{
		if (targ.constructor.toString().indexOf('Array') > -1)
		{
			for (var j = 0, jj = targ.length; j < jj; j++)
			{
				for (var i = 0, ii = this.objects.length; i < ii; i++)
				{
					if (this.objects[i].target == targ[j])
					{
						delete this.objects.splice(i, 1);
					}
				}
			}
		}
		else
		{
			for (var i = 0, ii = this.objects.length; i < ii; i++)
			{
				if (this.objects[i].target == targ)
				{
					delete this.objects.splice(i, 1);
				}
			}
		}
	},
	removeAllTweens: function ()
	{
		while (this.objects.length > 0)
		{
			delete this.objects.pop();
		}
	}
};
cheddar.tweener.Utils = {
	bezier2: function (t, p0, p1, p2)
	{
		return (1 - t) * (1 - t) * p0 + 2 * t * (1 - t) * p1 + t * t * p2;
	},
	bezier3: function (t, p0, p1, p2, p3)
	{
		return Math.pow(1 - t, 3) * p0 + 3 * t * Math.pow(1 - t, 2) * p1 + 3 * t * t * (1 - t) * p2 + t * t * t * p3;
	},
	allSetStyleProperties: function (element)
	{
		var css;
		if (document.defaultView && document.defaultView.getComputedStyle)
		{
			css = document.defaultView.getComputedStyle(element, null);
		}
		else
		{
			css = element.currentStyle;
		}
		for (var key in css)
		{
			if (!key.match(/^\d+$/))
			{
				try
				{
					element.style[key] = css[key];
				}
				catch (e)
				{}
			}
		}
	}
};
cheddar.tweener.easingFunctions = {
	easeNone: function (t, b, c, d)
	{
		return c * t / d + b;
	},
	easeInQuad: function (t, b, c, d)
	{
		return c * (t /= d) * t + b;
	},
	easeOutQuad: function (t, b, c, d)
	{
		return -c * (t /= d) * (t - 2) + b;
	},
	easeInOutQuad: function (t, b, c, d)
	{
		if ((t /= d / 2) < 1) return c / 2 * t * t + b;
		return -c / 2 * ((--t) * (t - 2) - 1) + b;
	},
	easeInCubic: function (t, b, c, d)
	{
		return c * (t /= d) * t * t + b;
	},
	easeOutCubic: function (t, b, c, d)
	{
		return c * ((t = t / d - 1) * t * t + 1) + b;
	},
	easeInOutCubic: function (t, b, c, d)
	{
		if ((t /= d / 2) < 1) return c / 2 * t * t * t + b;
		return c / 2 * ((t -= 2) * t * t + 2) + b;
	},
	easeOutInCubic: function (t, b, c, d)
	{
		if (t < d / 2) return cheddar.tweener.easingFunctions.easeOutCubic(t * 2, b, c / 2, d);
		return cheddar.tweener.easingFunctions.easeInCubic((t * 2) - d, b + c / 2, c / 2, d);
	},
	easeInQuart: function (t, b, c, d)
	{
		return c * (t /= d) * t * t * t + b;
	},
	easeOutQuart: function (t, b, c, d)
	{
		return -c * ((t = t / d - 1) * t * t * t - 1) + b;
	},
	easeInOutQuart: function (t, b, c, d)
	{
		if ((t /= d / 2) < 1) return c / 2 * t * t * t * t + b;
		return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
	},
	easeOutInQuart: function (t, b, c, d)
	{
		if (t < d / 2) return cheddar.tweener.easingFunctions.easeOutQuart(t * 2, b, c / 2, d);
		return cheddar.tweener.easingFunctions.easeInQuart((t * 2) - d, b + c / 2, c / 2, d);
	},
	easeInQuint: function (t, b, c, d)
	{
		return c * (t /= d) * t * t * t * t + b;
	},
	easeOutQuint: function (t, b, c, d)
	{
		return c * ((t = t / d - 1) * t * t * t * t + 1) + b;
	},
	easeInOutQuint: function (t, b, c, d)
	{
		if ((t /= d / 2) < 1) return c / 2 * t * t * t * t * t + b;
		return c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
	},
	easeOutInQuint: function (t, b, c, d)
	{
		if (t < d / 2) return cheddar.tweener.easingFunctions.easeOutQuint(t * 2, b, c / 2, d);
		return cheddar.tweener.easingFunctions.easeInQuint((t * 2) - d, b + c / 2, c / 2, d);
	},
	easeInSine: function (t, b, c, d)
	{
		return -c * Math.cos(t / d * (Math.PI / 2)) + c + b;
	},
	easeOutSine: function (t, b, c, d)
	{
		return c * Math.sin(t / d * (Math.PI / 2)) + b;
	},
	easeInOutSine: function (t, b, c, d)
	{
		return -c / 2 * (Math.cos(Math.PI * t / d) - 1) + b;
	},
	easeOutInSine: function (t, b, c, d)
	{
		if (t < d / 2) return cheddar.tweener.easingFunctions.easeOutSine(t * 2, b, c / 2, d);
		return cheddar.tweener.easingFunctions.easeInSine((t * 2) - d, b + c / 2, c / 2, d);
	},
	easeInExpo: function (t, b, c, d)
	{
		return (t == 0) ? b : c * Math.pow(2, 10 * (t / d - 1)) + b - c * 0.001;
	},
	easeOutExpo: function (t, b, c, d)
	{
		return (t == d) ? b + c : c * 1.001 * (-Math.pow(2, -10 * t / d) + 1) + b;
	},
	easeInOutExpo: function (t, b, c, d)
	{
		if (t == 0) return b;
		if (t == d) return b + c;
		if ((t /= d / 2) < 1) return c / 2 * Math.pow(2, 10 * (t - 1)) + b - c * 0.0005;
		return c / 2 * 1.0005 * (-Math.pow(2, -10 * --t) + 2) + b;
	},
	easeOutInExpo: function (t, b, c, d)
	{
		if (t < d / 2) return cheddar.tweener.easingFunctions.easeOutExpo(t * 2, b, c / 2, d);
		return cheddar.tweener.easingFunctions.easeInExpo((t * 2) - d, b + c / 2, c / 2, d);
	},
	easeInCirc: function (t, b, c, d)
	{
		return -c * (Math.sqrt(1 - (t /= d) * t) - 1) + b;
	},
	easeOutCirc: function (t, b, c, d)
	{
		return c * Math.sqrt(1 - (t = t / d - 1) * t) + b;
	},
	easeInOutCirc: function (t, b, c, d)
	{
		if ((t /= d / 2) < 1) return -c / 2 * (Math.sqrt(1 - t * t) - 1) + b;
		return c / 2 * (Math.sqrt(1 - (t -= 2) * t) + 1) + b;
	},
	easeOutInCirc: function (t, b, c, d)
	{
		if (t < d / 2) return cheddar.tweener.easingFunctions.easeOutCirc(t * 2, b, c / 2, d);
		return cheddar.tweener.easingFunctions.easeInCirc((t * 2) - d, b + c / 2, c / 2, d);
	},
	easeInElastic: function (t, b, c, d, a, p)
	{
		var s;
		if (t == 0) return b;
		if ((t /= d) == 1) return b + c;
		if (!p) p = d * .3;
		if (!a || a < Math.abs(c))
		{
			a = c;
			s = p / 4;
		}
		else s = p / (2 * Math.PI) * Math.asin(c / a);
		return -(a * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
	},
	easeOutElastic: function (t, b, c, d, a, p)
	{
		var s;
		if (t == 0) return b;
		if ((t /= d) == 1) return b + c;
		if (!p) p = d * .3;
		if (!a || a < Math.abs(c))
		{
			a = c;
			s = p / 4;
		}
		else s = p / (2 * Math.PI) * Math.asin(c / a);
		return (a * Math.pow(2, -10 * t) * Math.sin((t * d - s) * (2 * Math.PI) / p) + c + b);
	},
	easeInOutElastic: function (t, b, c, d, a, p)
	{
		var s;
		if (t == 0) return b;
		if ((t /= d / 2) == 2) return b + c;
		if (!p) p = d * (.3 * 1.5);
		if (!a || a < Math.abs(c))
		{
			a = c;
			s = p / 4;
		}
		else s = p / (2 * Math.PI) * Math.asin(c / a);
		if (t < 1) return -.5 * (a * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
		return a * Math.pow(2, -10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p) * .5 + c + b;
	},
	easeOutInElastic: function (t, b, c, d, a, p)
	{
		if (t < d / 2) return cheddar.tweener.easingFunctions.easeOutElastic(t * 2, b, c / 2, d, a, p);
		return cheddar.tweener.easingFunctions.easeInElastic((t * 2) - d, b + c / 2, c / 2, d, a, p);
	},
	easeInBack: function (t, b, c, d, s)
	{
		if (s == undefined) s = 1.70158;
		return c * (t /= d) * t * ((s + 1) * t - s) + b;
	},
	easeOutBack: function (t, b, c, d, s)
	{
		if (s == undefined) s = 1.70158;
		return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
	},
	easeInOutBack: function (t, b, c, d, s)
	{
		if (s == undefined) s = 1.70158;
		if ((t /= d / 2) < 1) return c / 2 * (t * t * (((s *= (1.525)) + 1) * t - s)) + b;
		return c / 2 * ((t -= 2) * t * (((s *= (1.525)) + 1) * t + s) + 2) + b;
	},
	easeOutInBack: function (t, b, c, d, s)
	{
		if (t < d / 2) return cheddar.tweener.easingFunctions.easeOutBack(t * 2, b, c / 2, d, s);
		return cheddar.tweener.easingFunctions.easeInBack((t * 2) - d, b + c / 2, c / 2, d, s);
	},
	easeInBounce: function (t, b, c, d)
	{
		return c - cheddar.tweener.easingFunctions.easeOutBounce(d - t, 0, c, d) + b;
	},
	easeOutBounce: function (t, b, c, d)
	{
		if ((t /= d) < (1 / 2.75))
		{
			return c * (7.5625 * t * t) + b;
		}
		else if (t < (2 / 2.75))
		{
			return c * (7.5625 * (t -= (1.5 / 2.75)) * t + .75) + b;
		}
		else if (t < (2.5 / 2.75))
		{
			return c * (7.5625 * (t -= (2.25 / 2.75)) * t + .9375) + b;
		}
		else
		{
			return c * (7.5625 * (t -= (2.625 / 2.75)) * t + .984375) + b;
		}
	},
	easeInOutBounce: function (t, b, c, d)
	{
		if (t < d / 2) return cheddar.tweener.easingFunctions.easeInBounce(t * 2, 0, c, d) * .5 + b;
		else return cheddar.tweener.easingFunctions.easeOutBounce(t * 2 - d, 0, c, d) * .5 + c * .5 + b;
	},
	easeOutInBounce: function (t, b, c, d)
	{
		if (t < d / 2) return cheddar.tweener.easingFunctions.easeOutBounce(t * 2, b, c / 2, d);
		return cheddar.tweener.easingFunctions.easeInBounce((t * 2) - d, b + c / 2, c / 2, d);
	}
};
cheddar.tweener.easingFunctions.linear = cheddar.tweener.easingFunctions.easeNone;
cheddar.AniSequence = cheddar.Class(
{
	fps: 0,
	frames: 0,
	timers: null,
	keyframes: null,
	running: false,
	onstart: null,
	onstop: null,
	initialize: function (fps, f)
	{
		this.fps = fps;
		this.frames = f;
		this.timers = new Array();
		this.keyframes = new Array();
	},
	run: function ()
	{
		var ASeq = this;
		ASeq.timer || (ASeq.timer = setInterval(function ()
		{
			for (var h, d = +(new Date), t = ASeq.timers, i = t.length; i--;)
			{
				for (var j = 0; j < ASeq.keyframes.length; j++)
				{
					if (ASeq.keyframes[j].frame == t[i].current)
					{
						ASeq.keyframes[j].method.apply(ASeq, ASeq.keyframes[j].args);
					}
				}
				t[i].current++;
				if (t[i].current >= ASeq.frames)
				{
					ASeq.stop(1);
				}
			}
		}, 1000 / this.fps));
	},
	addKeyframe: function (frame, method, args)
	{
		this.frames = Math.max(frame, this.frames);
		this.keyframes.push(
		{
			frame: frame,
			method: method,
			args: args
		});
	},
	start: function (c)
	{
		if (this.running) return;
		this.running = true, this.current = c || 0;
		this.time = new Date, this.onstart && this.onstart();
		if (this.frames <= 0 || this.fps <= 0) return this.stop(1);
		this.timers.push(this), this.run();
	},
	stop: function (r)
	{
		this.keyframes = null;
		this.running = false;
		if (this.timers.length) this.timer = clearInterval(this.timer), null;
		if (r)
		{
			this.onstop && this.onstop();
		}
	}
});
if (!Math.sinh)
{
	Math.sinh = function (x)
	{
		return 0.5 * (Math.exp(x) - Math.exp(-x));
	};
	Math.asinh = function (x)
	{
		return Math.log(x + Math.sqrt(x * x + 1));
	};
}
if (!Math.cosh)
{
	Math.cosh = function (x)
	{
		return 0.5 * (Math.exp(x) + Math.exp(-x));
	};
	Math.acosh = function (x)
	{
		return Math.log(x + Math.sqrt(x * x - 1));
	};
}
Drawable = cheddar.Class(CanvasNode, {
	pickable: true,
	strokeMode: 'above',
	ABOVE: 'above',
	BELOW: 'below',
	INSIDE: 'inside',
	initialize: function (config)
	{
		CanvasNode.initialize.call(this, config);
	},
	drawPickingPath: function (ctx)
	{
		if (!this.drawGeometry) return;
		ctx.beginPath();
		this.drawGeometry(ctx);
	},
	isPointInPath: function (x, y)
	{
		return false;
	},
	isVisible: function (ctx)
	{
		var abb = this.getAxisAlignedBoundingBox();
		if (!abb) return true;
		var x1 = abb[0],
			x2 = abb[0] + abb[2],
			y1 = abb[1],
			y2 = abb[1] + abb[3];
		var w = this.root.width;
		var h = this.root.height;
		if (this.root.drawBoundingBoxes)
		{
			ctx.save();
			var bbox = this.getBoundingBox();
			ctx.beginPath();
			ctx.rect(bbox[0], bbox[1], bbox[2], bbox[3]);
			ctx.strokeStyle = 'green';
			ctx.lineWidth = 1;
			ctx.stroke();
			ctx.restore();
			ctx.save();
			CanvasSupport.setTransform(ctx, [1, 0, 0, 1, 0, 0], this.currentMatrix);
			ctx.beginPath();
			ctx.rect(x1, y1, x2 - x1, y2 - y1);
			ctx.strokeStyle = 'red';
			ctx.lineWidth = 1.5;
			ctx.stroke();
			ctx.restore();
		}
		var visible = !(x2 < 0 || x1 > w || y2 < 0 || y1 > h);
		return visible;
	},
	createSubtreePath: function (ctx, skipTransform)
	{
		ctx.save();
		if (!skipTransform) this.transform(ctx, true);
		if (this.drawGeometry) this.drawGeometry(ctx);
		for (var i = 0; i < this.childNodes.length; i++)
		this.childNodes[i].createSubtreePath(ctx);
		ctx.restore();
	},
	draw: function (ctx)
	{
		if (!this.drawGeometry) return;
		if (this.opacity == 0) return;
		if (this.root.drawBoundingBoxes) this.isVisible(ctx);
		var ft = (ctx.fillStyle.transformList || ctx.fillStyle.matrix || ctx.fillStyle.scale != null || ctx.fillStyle.rotation || ctx.fillStyle.x || ctx.fillStyle.y);
		var st = (ctx.strokeStyle.transformList || ctx.strokeStyle.matrix || ctx.strokeStyle.scale != null || ctx.strokeStyle.rotation || ctx.strokeStyle.x || ctx.strokeStyle.y);
		ctx.beginPath();
		this.drawGeometry(ctx);
		if (ctx.strokeOn)
		{
			switch (this.strokeMode)
			{
			case this.ABOVE:
				if (ctx.fillOn) this.doFill(ctx, ft);
				this.doStroke(ctx, st);
				break;
			case this.BELOW:
				this.doStroke(ctx, st);
				if (ctx.fillOn) this.doFill(ctx, ft);
				break;
			case this.INSIDE:
				if (ctx.fillOn) this.doFill(ctx, ft);
				ctx.save();
				var lw = ctx.lineWidth;
				ctx.setLineWidth(1);
				this.doStroke(ctx, st);
				ctx.setLineWidth(lw);
				ctx.clip();
				this.doStroke(ctx, st);
				ctx.restore();
				break;
			}
		}
		else if (ctx.fillOn)
		{
			this.doFill(ctx, ft);
		}
		this.drawMarkers(ctx);
		if (this.clip) ctx.clip();
	},
	doFill: function (ctx, ft)
	{
		if (ft || (this.getBoundingBox && ctx.fillStyle.units == this.OBJECTBOUNDINGBOX))
		{
			ctx.save();
			if (this.getBoundingBox && ctx.fillStyle.units == this.OBJECTBOUNDINGBOX)
			{
				var bb = this.getBoundingBox();
				var sx = bb[2];
				var sy = bb[3];
				ctx.translate(bb[0], bb[1]);
				ctx.scale(sx, sy);
			}
			ctx.fillStyle.transform(ctx);
		}
		if (this.fillOpacity != null)
		{
			var go = ctx.globalAlpha;
			ctx.setGlobalAlpha(go * this.fillOpacity);
			ctx.fill();
			ctx.globalAlpha = go;
		}
		else
		{
			ctx.fill();
		}
		if (ft) ctx.restore();
	},
	doStroke: function (ctx, st)
	{
		if (st || (this.getBoundingBox && ctx.strokeStyle.units == this.OBJECTBOUNDINGBOX))
		{
			ctx.save();
			if (this.getBoundingBox && ctx.strokeStyle.units == this.OBJECTBOUNDINGBOX)
			{
				var bb = this.getBoundingBox();
				var sx = bb[2];
				var sy = bb[3];
				ctx.translate(bb[0], bb[1]);
				ctx.scale(sx, sy);
			}
			ctx.strokeStyle.needMatrixUpdate = true;
			ctx.strokeStyle.transform(ctx);
			if (sx != null) CanvasSupport.tScale(ctx.strokeStyle.currentMatrix, sx, sy);
			var cm = ctx.strokeStyle.currentMatrix;
			var sw = Math.sqrt(Math.max(cm[0] * cm[0] + cm[1] * cm[1], cm[2] * cm[2] + cm[3] * cm[3]));
			ctx.setLineWidth(((ctx.lineWidth == null) ? 1 : ctx.lineWidth) / sw);
		}
		if (this.strokeOpacity != null)
		{
			var go = ctx.globalAlpha;
			ctx.setGlobalAlpha(go * this.strokeOpacity);
			ctx.stroke();
			ctx.globalAlpha = go;
		}
		else
		{
			ctx.stroke();
		}
		if (st) ctx.restore();
	},
	drawMarkers: function (ctx)
	{
		var sm = this.markerStart || this.marker;
		var em = this.markerEnd || this.marker;
		var mm = this.markerMid || this.marker;
		if (sm && this.getStartPoint)
		{
			var pa = this.getStartPoint();
			if (sm.orient != null && sm.orient != 'auto') pa.angle = sm.orient;
			var scale = (sm.markerUnits == 'strokeWidth') ? ctx.lineWidth : 1;
			ctx.save();
			ctx.translate(pa.point[0], pa.point[1]);
			ctx.scale(scale, scale);
			ctx.rotate(pa.angle);
			var mat = CanvasSupport.tRotate(CanvasSupport.tScale(CanvasSupport.tTranslate(this.currentMatrix.slice(0), pa.point[0], pa.point[1]), scale, scale), pa.angle);
			sm.__copyMatrix(mat);
			sm.handleDraw(ctx);
			ctx.restore();
		}
		if (em && this.getEndPoint)
		{
			var pa = this.getEndPoint();
			if (em.orient != null && em.orient != 'auto') pa.angle = em.orient;
			var scale = (em.markerUnits == 'strokeWidth') ? ctx.lineWidth : 1;
			ctx.save();
			ctx.translate(pa.point[0], pa.point[1]);
			ctx.scale(scale, scale);
			ctx.rotate(pa.angle);
			var mat = CanvasSupport.tRotate(CanvasSupport.tScale(CanvasSupport.tTranslate(this.currentMatrix.slice(0), pa.point[0], pa.point[1]), scale, scale), pa.angle);
			em.__copyMatrix(mat);
			em.handleDraw(ctx);
			ctx.restore();
		}
		if (mm && this.getMidPoints)
		{
			var pas = this.getMidPoints();
			var scale = (mm.markerUnits == 'strokeWidth') ? ctx.lineWidth : 1;
			for (var i = 0; i < pas.length; i++)
			{
				var pa = pas[i];
				ctx.save();
				ctx.translate(pa.point[0], pa.point[1]);
				ctx.scale(scale, scale);
				if (mm.orient != null && mm.orient != 'auto') pa.angle = em.orient;
				ctx.rotate(pa.angle);
				var mat = CanvasSupport.tRotate(CanvasSupport.tScale(CanvasSupport.tTranslate(this.currentMatrix.slice(0), pa.point[0], pa.point[1]), scale, scale), pa.angle);
				mm.__copyMatrix(mat);
				mm.handleDraw(ctx);
				ctx.restore();
			}
		}
	},
	getStartPoint: false,
	getEndPoint: false,
	getMidPoints: false,
	getBoundingBox: false
});
Rectangle = cheddar.Class(Drawable, {
	cx: 0,
	cy: 0,
	x2: 0,
	y2: 0,
	width: 0,
	height: 0,
	rx: 0,
	ry: 0,
	centered: false,
	initialize: function (width, height, config)
	{
		if (width != null)
		{
			this.width = width;
			this.height = width;
		}
		if (height != null) this.height = height;
		Drawable.initialize.call(this, config);
	},
	drawGeometry: function (ctx)
	{
		var x = this.cx;
		var y = this.cy;
		var w = (this.width || (this.x2 - x));
		var h = (this.height || (this.y2 - y));
		if (w == 0 || h == 0) return;
		if (this.centered)
		{
			x -= 0.5 * w;
			y -= 0.5 * h;
		}
		if (this.rx || this.ry)
		{
			var rx = Math.min(w * 0.5, this.rx || this.ry);
			var ry = Math.min(h * 0.5, this.ry || rx);
			var k = 0.5522847498;
			var krx = k * rx;
			var kry = k * ry;
			ctx.moveTo(x + rx, y);
			ctx.lineTo(x - rx + w, y);
			ctx.bezierCurveTo(x - rx + w + krx, y, x + w, y + ry - kry, x + w, y + ry);
			ctx.lineTo(x + w, y + h - ry);
			ctx.bezierCurveTo(x + w, y + h - ry + kry, x - rx + w + krx, y + h, x - rx + w, y + h);
			ctx.lineTo(x + rx, y + h);
			ctx.bezierCurveTo(x + rx - krx, y + h, x, y + h - ry + kry, x, y + h - ry);
			ctx.lineTo(x, y + ry);
			ctx.bezierCurveTo(x, y + ry - kry, x + rx - krx, y, x + rx, y);
			ctx.closePath();
		}
		else
		{
			if (w < 0) x += w;
			if (h < 0) y += h;
			ctx.rect(x, y, Math.abs(w), Math.abs(h));
		}
	},
	isPointInPath: function (x, y)
	{
		x -= this.cx;
		y -= this.cy;
		if (this.centered)
		{
			x += this.width / 2;
			y += this.height / 2;
		}
		return (x >= 0 && x <= this.width && y >= 0 && y <= this.height);
	},
	getBoundingBox: function ()
	{
		var x = this.cx;
		var y = this.cy;
		if (this.centered)
		{
			x -= this.width / 2;
			y -= this.height / 2;
		}
		return [x, y, this.width, this.height];
	}
});
Path = cheddar.Class(Drawable, {
	segments: [],
	closePath: false,
	initialize: function (segments, config)
	{
		this.segments = segments;
		Drawable.initialize.call(this, config);
	},
	drawGeometry: function (ctx)
	{
		var segments = this.getSegments();
		for (var i = 0; i < segments.length; i++)
		{
			var seg = segments[i];
			ctx[seg[0]].apply(ctx, seg[1]);
		}
		if (this.closePath) ctx.closePath();
	},
	isPointInPath: function (px, py)
	{
		var bbox = this.getBoundingBox();
		return (px >= bbox[0] && px <= bbox[0] + bbox[2] && py >= bbox[1] && py <= bbox[1] + bbox[3]);
	},
	getBoundingBox: function ()
	{
		if (!(this.compiled && this.compiledBoundingBox))
		{
			var minX = Infinity,
				minY = Infinity,
				maxX = -Infinity,
				maxY = -Infinity;
			var segments = this.getSegments();
			for (var i = 0; i < segments.length; i++)
			{
				var seg = segments[i][1];
				for (var j = 0; j < seg.length; j += 2)
				{
					var x = seg[j],
						y = seg[j + 1];
					if (x < minX) minX = x;
					if (x > maxX) maxX = x;
					if (y < minY) minY = y;
					if (y > maxY) maxY = y;
				}
			}
			this.compiledBoundingBox = [minX, minY, maxX - minX, maxY - minY];
		}
		return this.compiledBoundingBox;
	},
	getStartPoint: function ()
	{
		var segs = this.getSegments();
		if (!segs || !segs[0]) return {
			point: [0, 0],
			angle: 0
		};
		var fs = segs[0];
		var c = fs[1];
		var point = [c[c.length - 2], c[c.length - 1]];
		var ss = segs[1];
		var angle = 0;
		if (ss)
		{
			c2 = ss[1];
			angle = Curves.lineAngle(point, [c2[c2.length - 2], c2[c2.length - 1]]);
		}
		return {
			point: point,
			angle: angle
		};
	},
	getEndPoint: function ()
	{
		var segs = this.getSegments();
		if (!segs || !segs[0]) return {
			point: [0, 0],
			angle: 0
		};
		var fs = segs[segs.length - 1];
		var c = fs[1];
		var point = [c[c.length - 2], c[c.length - 1]];
		var ss = segs[segs.length - 2];
		var angle = 0;
		if (ss)
		{
			c2 = ss[1];
			angle = Curves.lineAngle([c2[c2.length - 2], c2[c2.length - 1]], point);
		}
		return {
			point: point,
			angle: angle
		};
	},
	getMidPoints: function ()
	{
		var segs = this.getSegments();
		if (this.vertices) return this.vertices.slice(1, -1);
		var verts = [];
		for (var i = 1; i < segs.length - 1; i++)
		{
			var b = segs[i - 1][1].slice(-2);
			var c = segs[i][1].slice(0, 2);
			if (segs[i - 1].length > 2)
			{
				var a = segs[i - 1][1].slice(-4, -2);
				var t = 0.5 * (Curves.lineAngle(a, b) + Curves.lineAngle(b, c));
			}
			else
			{
				var t = Curves.lineAngle(b, c);
			}
			verts.push(
			{
				point: b,
				angle: t
			});
			var id = segs[i][2];
			if (id != null)
			{
				i++;
				while (segs[i] && segs[i][2] == id) i++;
				i--;
			}
		}
		return verts;
	},
	getSegments: function ()
	{
		if (typeof (this.segments) == 'string')
		{
			if (!this.compiled || this.segments != this.compiledSegments)
			{
				this.compiled = this.compileSVGPath(this.segments);
				this.compiledSegments = this.segments;
			}
		}
		else if (!this.compiled)
		{
			this.compiled = Object.clone(this.segments);
		}
		return this.compiled;
	},
	compileSVGPath: function (svgPath)
	{
		var segs = svgPath.split(/(?=[a-z])/i);
		var x = 0;
		var y = 0;
		var px, py;
		var pc;
		var commands = [];
		for (var i = 0; i < segs.length; i++)
		{
			var seg = segs[i];
			var cmd = seg.match(/[a-z]/i);
			if (!cmd) return [];
			cmd = cmd[0];
			var coords = seg.match(/[+-]?\d+(\.\d+(e\d+(\.\d+)?)?)?/gi);
			if (coords) coords = coords.map(parseFloat);
			switch (cmd)
			{
			case 'M':
				x = coords[0];
				y = coords[1];
				px = py = null;
				commands.push(['moveTo', [x, y]]);
				break;
			case 'm':
				x += coords[0];
				y += coords[1];
				px = py = null;
				commands.push(['moveTo', [x, y]]);
				break;
			case 'L':
				x = coords[0];
				y = coords[1];
				px = py = null;
				commands.push(['lineTo', [x, y]]);
				break;
			case 'l':
				x += coords[0];
				y += coords[1];
				px = py = null;
				commands.push(['lineTo', [x, y]]);
				break;
			case 'H':
				x = coords[0];
				px = py = null;
				commands.push(['lineTo', [x, y]]);
				break;
			case 'h':
				x += coords[0];
				px = py = null;
				commands.push(['lineTo', [x, y]]);
				break;
			case 'V':
				y = coords[0];
				px = py = null;
				commands.push(['lineTo', [x, y]]);
				break;
			case 'v':
				y += coords[0];
				px = py = null;
				commands.push(['lineTo', [x, y]]);
				break;
			case 'C':
				x = coords[4];
				y = coords[5];
				px = coords[2];
				py = coords[3];
				commands.push(['bezierCurveTo', coords]);
				break;
			case 'c':
				commands.push(['bezierCurveTo', [coords[0] + x, coords[1] + y, coords[2] + x, coords[3] + y, coords[4] + x, coords[5] + y]]);
				px = x + coords[2];
				py = y + coords[3];
				x += coords[4];
				y += coords[5];
				break;
			case 'S':
				if (px == null || !pc.match(/[sc]/i))
				{
					px = x;
					py = y;
				}
				commands.push(['bezierCurveTo', [x - (px - x), y - (py - y), coords[0], coords[1], coords[2], coords[3]]]);
				px = coords[0];
				py = coords[1];
				x = coords[2];
				y = coords[3];
				break;
			case 's':
				if (px == null || !pc.match(/[sc]/i))
				{
					px = x;
					py = y;
				}
				commands.push(['bezierCurveTo', [x - (px - x), y - (py - y), x + coords[0], y + coords[1], x + coords[2], y + coords[3]]]);
				px = x + coords[0];
				py = y + coords[1];
				x += coords[2];
				y += coords[3];
				break;
			case 'Q':
				px = coords[0];
				py = coords[1];
				x = coords[2];
				y = coords[3];
				commands.push(['quadraticCurveTo', coords]);
				break;
			case 'q':
				commands.push(['quadraticCurveTo', [coords[0] + x, coords[1] + y, coords[2] + x, coords[3] + y]]);
				px = x + coords[0];
				py = y + coords[1];
				x += coords[2];
				y += coords[3];
				break;
			case 'T':
				if (px == null || !pc.match(/[qt]/i))
				{
					px = x;
					py = y;
				}
				else
				{
					px = x - (px - x);
					py = y - (py - y);
				}
				commands.push(['quadraticCurveTo', [px, py, coords[0], coords[1]]]);
				px = x - (px - x);
				py = y - (py - y);
				x = coords[0];
				y = coords[1];
				break;
			case 't':
				if (px == null || !pc.match(/[qt]/i))
				{
					px = x;
					py = y;
				}
				else
				{
					px = x - (px - x);
					py = y - (py - y);
				}
				commands.push(['quadraticCurveTo', [px, py, x + coords[0], y + coords[1]]]);
				x += coords[0];
				y += coords[1];
				break;
			case 'A':
				var arc_segs = this.solveArc(x, y, coords);
				for (var l = 0; l < arc_segs.length; l++) arc_segs[l][2] = i;
				commands.push.apply(commands, arc_segs);
				x = coords[5];
				y = coords[6];
				break;
			case 'a':
				coords[5] += x;
				coords[6] += y;
				var arc_segs = this.solveArc(x, y, coords);
				for (var l = 0; l < arc_segs.length; l++) arc_segs[l][2] = i;
				commands.push.apply(commands, arc_segs);
				x = coords[5];
				y = coords[6];
				break;
			case 'Z':
				commands.push(['closePath', []]);
				break;
			case 'z':
				commands.push(['closePath', []]);
				break;
			}
			pc = cmd;
		}
		return commands;
	},
	solveArc: function (x, y, coords)
	{
		var rx = coords[0];
		var ry = coords[1];
		var rot = coords[2];
		var large = coords[3];
		var sweep = coords[4];
		var ex = coords[5];
		var ey = coords[6];
		var segs = this.arcToSegments(ex, ey, rx, ry, large, sweep, rot, x, y);
		var retval = [];
		for (var i = 0; i < segs.length; i++)
		{
			retval.push(['bezierCurveTo', this.segmentToBezier.apply(this, segs[i])]);
		}
		return retval;
	},
	arcToSegments: function (x, y, rx, ry, large, sweep, rotateX, ox, oy)
	{
		var th = rotateX * (Math.PI / 180);
		var sin_th = Math.sin(th);
		var cos_th = Math.cos(th);
		rx = Math.abs(rx);
		ry = Math.abs(ry);
		var px = cos_th * (ox - x) * 0.5 + sin_th * (oy - y) * 0.5;
		var py = cos_th * (oy - y) * 0.5 - sin_th * (ox - x) * 0.5;
		var pl = (px * px) / (rx * rx) + (py * py) / (ry * ry);
		if (pl > 1)
		{
			pl = Math.sqrt(pl);
			rx *= pl;
			ry *= pl;
		}
		var a00 = cos_th / rx;
		var a01 = sin_th / rx;
		var a10 = (-sin_th) / ry;
		var a11 = (cos_th) / ry;
		var x0 = a00 * ox + a01 * oy;
		var y0 = a10 * ox + a11 * oy;
		var x1 = a00 * x + a01 * y;
		var y1 = a10 * x + a11 * y;
		var d = (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);
		var sfactor_sq = 1 / d - 0.25;
		if (sfactor_sq < 0) sfactor_sq = 0;
		var sfactor = Math.sqrt(sfactor_sq);
		if (sweep == large) sfactor = -sfactor;
		var xc = 0.5 * (x0 + x1) - sfactor * (y1 - y0);
		var yc = 0.5 * (y0 + y1) + sfactor * (x1 - x0);
		var th0 = Math.atan2(y0 - yc, x0 - xc);
		var th1 = Math.atan2(y1 - yc, x1 - xc);
		var th_arc = th1 - th0;
		if (th_arc < 0 && sweep == 1)
		{
			th_arc += 2 * Math.PI;
		}
		else if (th_arc > 0 && sweep == 0)
		{
			th_arc -= 2 * Math.PI;
		}
		var segments = Math.ceil(Math.abs(th_arc / (Math.PI * 0.5 + 0.001)));
		var result = [];
		for (var i = 0; i < segments; i++)
		{
			var th2 = th0 + i * th_arc / segments;
			var th3 = th0 + (i + 1) * th_arc / segments;
			result[i] = [xc, yc, th2, th3, rx, ry, sin_th, cos_th];
		}
		return result;
	},
	segmentToBezier: function (cx, cy, th0, th1, rx, ry, sin_th, cos_th)
	{
		var a00 = cos_th * rx;
		var a01 = -sin_th * ry;
		var a10 = sin_th * rx;
		var a11 = cos_th * ry;
		var th_half = 0.5 * (th1 - th0);
		var t = (8 / 3) * Math.sin(th_half * 0.5) * Math.sin(th_half * 0.5) / Math.sin(th_half);
		var x1 = cx + Math.cos(th0) - t * Math.sin(th0);
		var y1 = cy + Math.sin(th0) + t * Math.cos(th0);
		var x3 = cx + Math.cos(th1);
		var y3 = cy + Math.sin(th1);
		var x2 = x3 + t * Math.sin(th1);
		var y2 = y3 - t * Math.cos(th1);
		return [a00 * x1 + a01 * y1, a10 * x1 + a11 * y1, a00 * x2 + a01 * y2, a10 * x2 + a11 * y2, a00 * x3 + a01 * y3, a10 * x3 + a11 * y3];
	},
	getLength: function ()
	{
		var segs = this.getSegments();
		if (segs.arcLength == null)
		{
			segs.arcLength = 0;
			var x = 0,
				y = 0;
			for (var i = 0; i < segs.length; i++)
			{
				var args = segs[i][1];
				if (args.length < 2) continue;
				switch (segs[i][0])
				{
				case 'bezierCurveTo':
					segs[i][3] = Curves.cubicLength([x, y], [args[0], args[1]], [args[2], args[3]], [args[4], args[5]]);
					break;
				case 'quadraticCurveTo':
					segs[i][3] = Curves.quadraticLength([x, y], [args[0], args[1]], [args[2], args[3]]);
					break;
				case 'lineTo':
					segs[i][3] = Curves.lineLength([x, y], [args[0], args[1]]);
					break;
				}
				if (segs[i][3]) segs.arcLength += segs[i][3];
				x = args[args.length - 2];
				y = args[args.length - 1];
			}
		}
		return segs.arcLength;
	},
	pointAngleAt: function (t, config)
	{
		var segments = [];
		var segs = this.getSegments();
		var length = this.getLength();
		var x = 0,
			y = 0;
		for (var i = 0; i < segs.length; i++)
		{
			var seg = segs[i];
			if (seg[1].length < 2) continue;
			if (seg[0] != 'moveTo')
			{
				segments.push([x, y, seg]);
			}
			x = seg[1][seg[1].length - 2];
			y = seg[1][seg[1].length - 1];
		}
		if (segments.length < 1) return {
			point: [x, y],
			angle: 0
		};
		if (t >= 1)
		{
			var rt = 1;
			var seg = segments[segments.length - 1];
		}
		else if (config && config.discrete)
		{
			var idx = Math.floor(t * segments.length);
			var seg = segments[idx];
			var rt = 0;
		}
		else if (config && config.linear)
		{
			var idx = t * segments.length;
			var rt = idx - Math.floor(idx);
			var seg = segments[Math.floor(idx)];
		}
		else
		{
			var len = t * length;
			var rlen = 0,
				idx, rt;
			for (var i = 0; i < segments.length; i++)
			{
				if (rlen + segments[i][2][3] > len)
				{
					idx = i;
					rt = (len - rlen) / segments[i][2][3];
					break;
				}
				rlen += segments[i][2][3];
			}
			var seg = segments[idx];
		}
		var angle = 0;
		var cmd = seg[2][0];
		var args = seg[2][1];
		switch (cmd)
		{
		case 'bezierCurveTo':
			return Curves.cubicLengthPointAngle([seg[0], seg[1]], [args[0], args[1]], [args[2], args[3]], [args[4], args[5]], rt);
			break;
		case 'quadraticCurveTo':
			return Curves.quadraticLengthPointAngle([seg[0], seg[1]], [args[0], args[1]], [args[2], args[3]], rt);
			break;
		case 'lineTo':
			x = Curves.linearValue(seg[0], args[0], rt);
			y = Curves.linearValue(seg[1], args[1], rt);
			angle = Curves.lineAngle([seg[0], seg[1]], [args[0], args[1]], rt);
			break;
		}
		return {
			point: [x, y],
			angle: angle
		};
	}
});
Circle = cheddar.Class(Drawable, {
	cx: 0,
	cy: 0,
	radius: 10,
	startAngle: 0,
	endAngle: Math.PI * 2,
	clockwise: false,
	closePath: true,
	includeCenter: false,
	initialize: function (radius, config)
	{
		if (radius != null) this.radius = radius;
		Drawable.initialize.call(this, config);
	},
	drawGeometry: function (ctx)
	{
		if (this.radius == 0) return;
		if (this.includeCenter) ctx.moveTo(this.cx, this.cy);
		ctx.arc(this.cx, this.cy, this.radius, this.startAngle, this.endAngle, this.clockwise);
		if (this.closePath)
		{
			var x2 = Math.cos(this.endAngle);
			var y2 = Math.sin(this.endAngle);
			ctx.moveTo(this.cx + x2 * this.radius, this.cy + y2 * this.radius);
			ctx.closePath();
		}
	},
	isPointInPath: function (x, y)
	{
		x -= this.cx;
		y -= this.cy;
		return (x * x + y * y) <= (this.radius * this.radius);
	},
	getBoundingBox: function ()
	{
		return [this.cx - this.radius, this.cy - this.radius, 2 * this.radius, 2 * this.radius];
	}
});
Polygon = cheddar.Class(Drawable, {
	segments: [],
	closePath: true,
	initialize: function (segments, config)
	{
		this.segments = segments;
		Drawable.initialize.call(this, config);
	},
	drawGeometry: function (ctx)
	{
		if (!this.segments || this.segments.length < 2) return;
		var s = this.segments;
		ctx.moveTo(s[0], s[1]);
		for (var i = 2; i < s.length; i += 2)
		{
			ctx.lineTo(s[i], s[i + 1]);
		}
		if (this.closePath) ctx.closePath();
	},
	isPointInPath: function (px, py)
	{
		if (!this.segments || this.segments.length < 2) return false;
		var bbox = this.getBoundingBox();
		return (px >= bbox[0] && px <= bbox[0] + bbox[2] && py >= bbox[1] && py <= bbox[1] + bbox[3]);
	},
	getStartPoint: function ()
	{
		if (!this.segments || this.segments.length < 2) return {
			point: [0, 0],
			angle: 0
		};
		var a = 0;
		if (this.segments.length > 2)
		{
			a = Curves.lineAngle(this.segments.slice(0, 2), this.segments.slice(2, 4));
		}
		return {
			point: this.segments.slice(0, 2),
			angle: a
		};
	},
	getEndPoint: function ()
	{
		if (!this.segments || this.segments.length < 2) return {
			point: [0, 0],
			angle: 0
		};
		var a = 0;
		if (this.segments.length > 2)
		{
			a = Curves.lineAngle(this.segments.slice(-4, -2), this.segments.slice(-2));
		}
		return {
			point: this.segments.slice(-2),
			angle: a
		};
	},
	getMidPoints: function ()
	{
		if (!this.segments || this.segments.length < 2) return [];
		var segs = this.segments;
		var verts = [];
		for (var i = 2; i < segs.length - 2; i += 2)
		{
			var a = segs.slice(i - 2, i);
			var b = segs.slice(i, i + 2);
			var c = segs.slice(i + 2, i + 4);
			var t = 0.5 * (Curves.lineAngle(a, b) + Curves.lineAngle(b, c));
			verts.push(
			{
				point: b,
				angle: t
			});
		}
		return verts;
	},
	getBoundingBox: function ()
	{
		var minX = Infinity,
			minY = Infinity,
			maxX = -Infinity,
			maxY = -Infinity;
		var s = this.segments;
		for (var i = 0; i < s.length; i += 2)
		{
			var x = s[i],
				y = s[i + 1];
			if (x < minX) minX = x;
			if (x > maxX) maxX = x;
			if (y < minY) minY = y;
			if (y > maxY) maxY = y;
		}
		return [minX, minY, maxX - minX, maxY - minY];
	}
});
ImageNode = cheddar.Class(Drawable, {
	centered: false,
	usePattern: false,
	sX: 0,
	sY: 0,
	sWidth: null,
	sHeight: null,
	dX: 0,
	dY: 0,
	dWidth: null,
	dHeight: null,
	initialize: function (image, config)
	{
		this.image = image;
		Drawable.initialize.call(this, config);
	},
	drawGeometry: function (ctx)
	{
		if (Object.isImageLoaded(this.image))
		{
			var w = this.dWidth == null ? this.image.width : this.dWidth;
			var h = this.dHeight == null ? this.image.height : this.dHeight;
			var x = this.dX + (this.centered ? -w * 0.5 : 0);
			var y = this.dY + (this.centered ? -h * 0.5 : 0);
			if (this.dWidth != null)
			{
				if (this.sWidth != null)
				{
					ctx.drawImage(this.image, this.sX, this.sY, this.sWidth, this.sHeight, x, y, w, h);
				}
				else
				{
					ctx.drawImage(this.image, x, y, w, h);
				}
			}
			else
			{
				w = this.image.width;
				h = this.image.height;
				if (this.usePattern)
				{
					if (!this.imagePattern) this.imagePattern = new Pattern(this.image, 'repeat');
					var fs = this.imagePattern.compiled;
					if (!fs) fs = this.imagePattern.compile(ctx);
					ctx.save();
					ctx.beginPath();
					ctx.rect(x, y, w, h);
					ctx.setFillStyle(fs);
					ctx.fill();
					ctx.restore();
					ctx.beginPath();
				}
				else
				{
					ctx.drawImage(this.image, x, y);
				}
			}
		}
		else
		{
			var w = this.dWidth;
			var h = this.dHeight;
			if (!(w && h)) return;
			var x = this.dX + (this.centered ? -w * 0.5 : 0);
			var y = this.dY + (this.centered ? -h * 0.5 : 0);
		}
		ctx.rect(x, y, w, h);
	},
	drawPickingPath: function (ctx)
	{
		var x = this.dX + (this.centered ? -this.image.width * 0.5 : 0);
		var y = this.dY + (this.centered ? -this.image.height * 0.5 : 0);
		var w = this.dWidth;
		var h = this.dHeight;
		if (this.dWidth == null)
		{
			w = this.image.width;
			h = this.image.height;
		}
		ctx.rect(x, y, w, h);
	},
	isPointInPath: function (x, y)
	{
		x -= this.dX;
		y -= this.dY;
		if (this.centered)
		{
			x += this.image.width * 0.5;
			y += this.image.height * 0.5;
		}
		var w = this.dWidth;
		var h = this.dHeight;
		if (this.dWidth == null)
		{
			w = this.image.width;
			h = this.image.height;
		}
		return ((x >= 0) && (x <= w) && (y >= 0) && (y <= h));
	},
	getBoundingBox: function ()
	{
		x = this.dX;
		y = this.dY;
		if (this.centered)
		{
			x -= this.image.width * 0.5;
			y -= this.image.height * 0.5;
		}
		var w = this.dWidth;
		var h = this.dHeight;
		if (this.dWidth == null)
		{
			w = this.image.width;
			h = this.image.height;
		}
		return [x, y, w, h];
	}
});
ImageNode.load = function (src)
{
	var img = new Image();
	img.src = src;
	var imgn = new ImageNode(img);
	return imgn;
};
Gradient = cheddar.Class(
{
	type: 'linear',
	isPattern: true,
	startX: 0,
	startY: 0,
	endX: 1,
	endY: 0,
	startRadius: 0,
	endRadius: 1,
	colorStops: [],
	initialize: function (config)
	{
		this.colorStops = [
			[0, '#000000'],
			[1, '#FFFFFF']
		];
		if (config) Object.extend(this, config);
	},
	compile: function (ctx)
	{
		if (this.type == 'linear')
		{
			var go = ctx.createLinearGradient(this.startX, this.startY, this.endX, this.endY);
		}
		else
		{
			var go = ctx.createRadialGradient(this.startX, this.startY, this.startRadius, this.endX, this.endY, this.endRadius);
		}
		for (var i = 0; i < this.colorStops.length; i++)
		{
			var cs = this.colorStops[i];
			if (typeof (cs[1]) == 'string')
			{
				go.addColorStop(cs[0], cs[1]);
			}
			else
			{
				var ca = cs[1];
				var a = (ca.length == 3) ? 1 : ca[3];
				var g = 'rgba(' + ca.slice(0, 3).map(Math.round).join(',') + ', ' + a + ')';
				go.addColorStop(cs[0], g);
			}
		}
		Object.extend(go, Transformable.prototype);
		go.transformList = this.transformList;
		go.scale = this.scale;
		go.x = this.x;
		go.y = this.y;
		go.matrix = this.matrix;
		go.rotation = this.rotation;
		go.units = this.units;
		if (!go.isMockObject) this.compiled = go;
		return go;
	}
});

var androidExternal = androidExternal || {};
androidExternal.animations = androidExternal.animations || {};
androidExternal.animations.footer = cheddar.Class(CanvasNode, {
	initialize: function (canvas)
	{
		CanvasNode.initialize.call(this);
		this.canvas = new Canvas(canvas);
		this.canvas.append(this);
		var t = this;
		this.canvas.removeEventListener("mouseover", function (event)
		{
			t.animateFooter()
		}, false);
		this.canvas.removeEventListener("click", function (event)
		{
			t.wave()
		}, false);
		this.canvas.addEventListener("mouseover", function (event)
		{
			t.animateFooter()
		}, false);
		this.canvas.addEventListener("click", function (event)
		{
			t.canvas.play();
			t.wave()
		}, false);
		this.setProps(
		{
			x: 25,
			y: 19
		});
		this.allow_footer_animation = true;
		this.is_animating = false;
		this.l_leg = new CanvasNode(
		{
			x: 14,
			y: 33,
			rotation: 0
		});
		var l1p = "M26.729,43.092c0,1.457-1.181,2.638-2.639,2.638l0,0c-1.456,0-2.";
		l1p += "638-1.182-2.638-2.638V31.237c0-1.457,1.182-2.638,2.638-2.638l0,0c";
		l1p += "1.458,0,2.639,1.182,2.639,2.638V43.092z";
		this.l_leg1 = new Path(Path.compileSVGPath(l1p), {
			fill: "#9C0",
			x: -24,
			y: -33
		});
		this.l_leg.append(this.l_leg1);
		this.r_leg = new CanvasNode(
		{
			x: 24,
			y: 33,
			rotation: 0
		});
		var r1p = "M16.547,43.092c0,1.457-1.181,2.638-2.638,2.638l0,0c-1.457,0-2.";
		r1p += "638-1.182-2.638-2.638V31.237c0-1.457,1.181-2.638,2.638-2.638l0,0c";
		r1p += "1.457,0,2.638,1.182,2.638,2.638V43.092z";
		this.r_leg1 = new Path(Path.compileSVGPath(r1p), {
			fill: "#9C0",
			x: -14,
			y: -33
		});
		this.r_leg.append(this.r_leg1);
		this.append(this.l_leg, this.r_leg);
		this.torso = new CanvasNode;
		var t1 = "M31.378,35.021c0,0.818-0.663,1.481-1.482,1.481H8.104c-0.818,0-1";
		t1 += ".483-0.663-1.483-1.481V15.583c0-0.818,0.665-1.481,1.483-1.481h21.7";
		t1 += "92c0.818,0,1.482,0.663,1.482,1.481V35.021L31.378,35.021z";
		this.torso1 = new Path(Path.compileSVGPath(t1), {
			fill: "#99cc00"
		});
		this.torso.append(this.torso1);
		this.torso2 = new Rectangle(24, 10, {
			fill: "#9C0",
			x: 6.5,
			y: 14
		});
		this.torso.append(this.torso2);
		this.append(this.torso);
		this.r_arm = new CanvasNode(
		{
			x: 35,
			y: 16
		});
		var r1 = "M38,27.75c0,1.456-1.181,2.638-2.638,2.638l0,0c-1.457,0-2.639-1.";
		r1 += "182-2.639-2.638V15.896c0-1.457,1.182-2.639,2.639-2.639l0,0c1.457,0";
		r1 += ",2.638,1.182,2.638,2.639V27.75z";
		this.r_arm_shape = new Path(Path.compileSVGPath(r1), {
			fill: "#9C0",
			stroke: "#FFF",
			x: -35,
			y: -16
		});
		this.r_arm.append(this.r_arm_shape);
		this.l_arm = new CanvasNode(
		{
			x: 3,
			y: 16
		});
		var l1 = "M5.276,27.75c0,1.456-1.181,2.638-2.638,2.638l0,0C1.181,30.388,0";
		l1 += ",29.206,0,27.75V15.896c0-1.457,1.181-2.639,2.638-2.639l0,0c1.457,0";
		l1 += ",2.638,1.182,2.638,2.639V27.75z";
		this.l_arm_path = new Path(Path.compileSVGPath(l1), {
			fill: "#9C0",
			stroke: "#FFF",
			x: -3,
			y: -16
		});
		this.l_arm.append(this.l_arm_path);
		this.append(this.r_arm, this.l_arm);
		this.head = new CanvasNode(
		{
			x: 10,
			y: 10,
			rotation: 0
		});
		var h1 = "M6.701,12.642c0,0,0.274-10.498,12.299-10.461c11.913,0.037,12.29";
		h1 += "9,10.461,12.299,10.461H6.701z";
		this.head_base = new Path(Path.compileSVGPath(h1), {
			fill: "#9C0",
			x: -10,
			y: -10
		});
		this.l_eye = new Circle(1.5, {
			x: 4,
			y: -2,
			fill: "#FFF"
		});
		this.r_eye = new Circle(1.5, {
			x: 14,
			y: -2,
			fill: "#FFF"
		});
		var a1 = "M13.194,3.592c0.043,0.062,0.178,0.042,0.301-0.046l0,0c0.121-0.0";
		a1 += "85,0.185-0.205,0.141-0.268l-2.298-3.242c-0.044-0.062-0.178-0.042-0";
		a1 += ".299,0.044l0,0c-0.123,0.087-0.186,0.206-0.142,0.269L13.194,3.592z";
		this.l_ant = new Path(Path.compileSVGPath(a1), {
			fill: "#9C0",
			x: -10,
			y: -10
		});
		var a2 = "M24.805,3.592c-0.043,0.062-0.178,0.042-0.301-0.046l0,0c-0.12-0.";
		a2 += "085-0.185-0.205-0.141-0.268l2.298-3.242c0.044-0.062,0.178-0.042,0.";
		a2 += "3,0.044l0,0c0.122,0.087,0.186,0.206,0.142,0.269L24.805,3.592z";
		this.r_ant = new Path(Path.compileSVGPath(a2), {
			fill: "#9C0",
			x: -10,
			y: -10
		});
		this.head.append(this.head_base, this.l_eye, this.r_eye, this.l_ant, this.r_ant);
		this.append(this.head)
	},
	wave: function ()
	{
		if (this.is_animating) return;
		this.is_animating = true;
		this.waveSequence = new cheddar.AniSequence(30, 40);
		this.waveSequence.addKeyframe(0, function (t)
		{
			cheddar.tweener.addTween(t.head, {
				time: 0.25,
				rotation: -0.1,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				rotation: -3,
				transition: "linear"
			})
		}, [this]);
		this.waveSequence.addKeyframe(9, function (t)
		{
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				rotation: -2.5,
				transition: "linear"
			})
		}, [this]);
		this.waveSequence.addKeyframe(18, function (t)
		{
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				rotation: -3,
				transition: "linear"
			})
		}, [this]);
		this.waveSequence.addKeyframe(27, function (t)
		{
			cheddar.tweener.addTween(t.head, {
				time: 0.25,
				rotation: 0,
				y: 10,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				rotation: 0,
				transition: "linear"
			})
		}, [this]);
		this.waveSequence.addKeyframe(39, function (t)
		{
			t.is_animating = false;
			t.canvas.stop()
		}, [this]);
		this.waveSequence.start()
	},
	semaphore: function ()
	{
		this.is_animating = true;
		this.lflag = new androidExternal.animations.footer.Flag;
		this.lflag.setProps(
		{
			rotation: 3.14,
			y: 26
		});
		this.rflag = new androidExternal.animations.footer.Flag;
		this.rflag.setProps(
		{
			rotation: 3.14,
			y: 26
		});
		this.semSeq = new cheddar.AniSequence(30, 126);
		this.semSeq.addKeyframe(0, function (t)
		{
			t.l_arm.zIndex = t.r_arm.zIndex = 3;
			t.l_arm.append(t.lflag);
			t.r_arm.append(t.rflag);
			t.lflag.flip();
			cheddar.tweener.addTween(t.l_arm, {
				time: 0.25,
				rotation: 0
			});
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				rotation: 0
			});
			cheddar.tweener.addTween(t.lflag, {
				time: 0.25,
				opacity: 1
			});
			cheddar.tweener.addTween(t.rflag, {
				time: 0.25,
				opacity: 1
			})
		}, [this]);
		this.semSeq.addKeyframe(20, function (t)
		{
			cheddar.tweener.addTween(t.l_arm, {
				time: 0.25,
				rotation: 3.14
			})
		}, [this]);
		this.semSeq.addKeyframe(35, function (t)
		{
			cheddar.tweener.addTween(t.l_arm, {
				time: 0.25,
				rotation: 1.57
			});
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				rotation: -1.57
			})
		}, [this]);
		this.semSeq.addKeyframe(50, function (t)
		{
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				rotation: -4
			});
			t.rflag.flip()
		}, [this]);
		this.semSeq.addKeyframe(60, function (t)
		{
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				rotation: -4
			})
		}, [this]);
		this.semSeq.addKeyframe(75, function (t)
		{
			cheddar.tweener.addTween(t.l_arm, {
				time: 0.25,
				rotation: 0.785
			})
		}, [this]);
		this.semSeq.addKeyframe(90, function (t)
		{
			t.rflag.flip();
			cheddar.tweener.addTween(t.l_arm, {
				time: 0.25,
				rotation: 3.14
			});
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				rotation: 0
			})
		}, [this]);
		this.semSeq.addKeyframe(105, function (t)
		{
			cheddar.tweener.addTween(t.l_arm, {
				time: 0.25,
				rotation: 0
			});
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				rotation: 0
			});
			cheddar.tweener.addTween(t.lflag, {
				time: 0.25,
				opacity: 0
			});
			cheddar.tweener.addTween(t.rflag, {
				time: 0.25,
				opacity: 0
			})
		}, [this]);
		this.semSeq.addKeyframe(125, function (t)
		{
			t.is_animating = false;
			t.canvas.stop()
		}, [this]);
		this.semSeq.start()
	},
	juggle: function ()
	{
		this.is_animating = true;
		this.balls = [new Circle(3, {
			fill: "#00F",
			x: -3,
			y: 10,
			opacity: 0
		}), new Circle(3, {
			fill: "#FFAD00",
			x: 3,
			y: 11,
			opacity: 0
		}), new Circle(3, {
			fill: "#c30202",
			y: 10,
			opacity: 0
		})];
		this.ball_coords = [
		{
			x: 0,
			y: 26
		}, {
			x: 6,
			y: 17
		}, {
			x: 35,
			y: 26
		}];
		this.juggle_frame = 0;
		this.juggle_intreval = null;
		this.doStop = false;
		this.seq = new cheddar.AniSequence(30, 102);
		this.seq.addKeyframe(0, function (t)
		{
			t.startJuggle();
			t.append(t.balls)
		}, [this]);
		this.seq.addKeyframe(72, function (t)
		{
			t.transitionOutJuggle()
		}, [this]);
		this.seq.addKeyframe(89, function (t)
		{
			t.stopJuggle()
		}, [this]);
		this.seq.addKeyframe(101, function (t)
		{
			t.is_animating = false;
			t.canvas.stop()
		}, [this]);
		this.seq.start();
		this.startJuggle = function ()
		{
			for (var i = 0; i < 3; i++) cheddar.tweener.addTween(this.balls[i], {
				time: 0.5,
				opacity: 1
			});
			this.juggle_interval = setInterval(function (t)
			{
				t.updateJuggle()
			}, 30, this)
		};
		this.updateJuggle = function ()
		{
			if (!this.doStop)
			{
				this.l_arm.rotation = Math.sin(this.juggle_frame / 2) / 2;
				this.r_arm.rotation = Math.sin((this.juggle_frame + 8) / 2) / 2
			}
			for (var i = 0; i < 3; i++)
			{
				if (this.balls == null) return;
				var ny = Math.sin((this.juggle_frame + i * 60) * 0.5) * 18;
				var nx = Math.cos((this.juggle_frame + i * 60) * 0.25) * 15;
				this.balls[i].y = 10 + ny;
				this.balls[i].x = 18 + nx
			}
			this.juggle_frame++
		};
		this.transitionOutJuggle = function ()
		{
			this.doStop = true;
			cheddar.tweener.addTween(this.l_arm, {
				time: 0.5,
				rotation: 0
			});
			cheddar.tweener.addTween(this.r_arm, {
				time: 0.5,
				rotation: 0
			});
			for (var i = 0; i < 3; i++) cheddar.tweener.addTween(this.balls[i], {
				time: 1,
				opacity: 0
			})
		};
		this.stopJuggle = function ()
		{
			setTimeout(function (t)
			{
				clearInterval(t.juggle_interval)
			}, 500, this);
			this.remove(this.balls);
			this.balls = null;
			this.ball_coords = null;
			this.seq = null
		}
	},
	moonwalk: function ()
	{
		this.is_animating = true;
		this.seq = new cheddar.AniSequence(30, 85);
		this.seq.addKeyframe(0, function (t)
		{
			t.l_arm.zIndex = 2;
			t.r_arm.zIndex = -1;
			cheddar.tweener.addTween(t.l_arm, {
				time: 0.5,
				x: 20
			});
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.5,
				x: 20
			});
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.5,
				x: 21
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.5,
				x: 19
			});
			cheddar.tweener.addTween(t.r_eye, {
				time: 0.5,
				x: 25
			});
			cheddar.tweener.addTween(t.l_eye, {
				time: 0.5,
				x: 15
			});
			cheddar.tweener.addTween(t, {
				time: 1,
				x: -5,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(5, function (t)
		{
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.25,
				rotation: 0.5,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.25,
				rotation: -0.5,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(12, function (t)
		{
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.25,
				rotation: -0.5,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.25,
				rotation: 0.5,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(18, function (t)
		{
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.25,
				rotation: 0.5,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.25,
				rotation: -0.5,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(24, function (t)
		{
			cheddar.tweener.addTween(t.l_arm, {
				time: 0.25,
				x: 3,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				x: 35,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.r_eye, {
				time: 0.25,
				x: 4,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_eye, {
				time: 0.25,
				x: -14,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.25,
				x: 14,
				rotation: 0,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.25,
				x: 24,
				rotation: 0,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(28, function (t)
		{
			t.l_arm.zIndex = -1;
			t.r_arm.zIndex = 2;
			cheddar.tweener.addTween(t.l_arm, {
				time: 0.25,
				x: 20,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				x: 20,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.r_eye, {
				time: 0.25,
				x: 4,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_eye, {
				time: 0.25,
				x: -17,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.25,
				x: 21,
				rotation: -0.5,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.25,
				x: 19,
				rotation: 0.5,
				transition: "linear"
			});
			cheddar.tweener.addTween(t, {
				time: 1,
				x: 25,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(34, function (t)
		{
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.25,
				rotation: 0.5,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.25,
				rotation: -0.5,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(40, function (t)
		{
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.25,
				rotation: -0.5,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.25,
				rotation: 0.5,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(46, function (t)
		{
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.25,
				rotation: 0.5,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.25,
				rotation: -0.5,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(52, function (t)
		{
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.25,
				rotation: 0.5,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.25,
				rotation: -0.5,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(58, function (t)
		{
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.25,
				rotation: -0.5,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.25,
				rotation: 0.5,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(62, function (t)
		{
			cheddar.tweener.addTween(t.l_arm, {
				time: 0.25,
				x: 3,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.r_arm, {
				time: 0.25,
				x: 35,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.r_eye, {
				time: 0.25,
				x: 14,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_eye, {
				time: 0.25,
				x: 4,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.r_leg, {
				time: 0.25,
				x: 24,
				rotation: 0,
				transition: "linear"
			});
			cheddar.tweener.addTween(t.l_leg, {
				time: 0.25,
				x: 14,
				rotation: 0,
				transition: "linear"
			})
		}, [this]);
		this.seq.addKeyframe(84, function (t)
		{
			t.is_animating = false;
			t.canvas.stop()
		}, [this]);
		this.seq.start()
	},
	animateFooter: function (e)
	{
		if (!this.allow_footer_animation || this.is_animating) return;
		var types = ["wave", "semaphore", "juggle", "moonwalk"];
		type = types[Math.floor(Math.random() * types.length)];
		this.canvas.play();
		if (type == "wave") this.wave();
		else if (type == "semaphore") this.semaphore();
		else if (type == "juggle") this.juggle();
		else if (type == "moonwalk") this.moonwalk();
		this.allow_footer_animation = false;
		setTimeout(function (t)
		{
			t.allow_footer_animation = true
		}, 5E3, this)
	}
});
androidExternal.animations.footer.Flag = cheddar.Class(CanvasNode, {
	initialize: function ()
	{
		CanvasNode.initialize.call(this);
		this.opacity = 0;
		this.pole = new Rectangle(1, 16, {
			fill: "#666"
		});
		this.flag = new Rectangle(14, 10, {
			fill: "#666"
		});
		this.scale = [1, 1];
		this.append(this.flag, this.pole)
	},
	flip: function ()
	{
		this.scale = [this.scale[0] * -1, 1]
	}
});

function testCanvas()
{
	var elem = document.createElement("canvas");
	return !!(elem.getContext && elem.getContext("2d"))
};

(function ()
{
	var i = void 0,
		l = true,
		n = null,
		o = false,
		p, q = this;

	function s()
	{}

	function t(a)
	{
		var b = typeof a;
		if (b == "object") if (a)
		{
			if (a instanceof Array) return "array";
			else if (a instanceof Object) return b;
			var d = Object.prototype.toString.call(a);
			if (d == "[object Window]") return "object";
			if (d == "[object Array]" || typeof a.length == "number" && typeof a.splice != "undefined" && typeof a.propertyIsEnumerable != "undefined" && !a.propertyIsEnumerable("splice")) return "array";
			if (d == "[object Function]" || typeof a.call != "undefined" && typeof a.propertyIsEnumerable != "undefined" && !a.propertyIsEnumerable("call")) return "function"
		}
		else return "null";
		else if (b == "function" && typeof a.call == "undefined") return "object";
		return b
	}
	var u = "closure_uid_" + Math.floor(Math.random() * 2147483648).toString(36),
		x = 0;

	function y(a, b)
	{
		function d()
		{}
		d.prototype = b.prototype;
		a.m = b.prototype;
		a.prototype = new d
	};

	function aa(a, b)
	{
		for (var d = 0, e = String(a).replace(/^[\s\xa0]+|[\s\xa0]+$/g, "").split("."), g = String(b).replace(/^[\s\xa0]+|[\s\xa0]+$/g, "").split("."), c = Math.max(e.length, g.length), f = 0; d == 0 && f < c; f++)
		{
			var h = e[f] || "",
				j = g[f] || "",
				k = RegExp("(\\d*)(\\D*)", "g"),
				v = RegExp("(\\d*)(\\D*)", "g");
			do
			{
				var m = k.exec(h) || ["", "", ""],
					r = v.exec(j) || ["", "", ""];
				if (m[0].length == 0 && r[0].length == 0) break;
				d = z(m[1].length == 0 ? 0 : parseInt(m[1], 10), r[1].length == 0 ? 0 : parseInt(r[1], 10)) || z(m[2].length == 0, r[2].length == 0) || z(m[2], r[2])
			} while (d == 0)
		}
		return d
	}
	function z(a, b)
	{
		if (a < b) return -1;
		else if (a > b) return 1;
		return 0
	};
	var A = Array.prototype,
		ba = A.indexOf ?
	function (a, b, d)
	{
		return A.indexOf.call(a, b, d)
	} : function (a, b, d)
	{
		d = d == n ? 0 : d < 0 ? Math.max(0, a.length + d) : d;
		if (typeof a == "string") return typeof b != "string" || b.length != 1 ? -1 : a.indexOf(b, d);
		for (; d < a.length; d++) if (d in a && a[d] === b) return d;
		return -1
	};
	var B, C, D, ca;

	function da()
	{
		return q.navigator ? q.navigator.userAgent : n
	}
	ca = D = C = B = o;
	var E;
	if (E = da())
	{
		var ea = q.navigator;
		B = E.indexOf("Opera") == 0;
		C = !B && E.indexOf("MSIE") != -1;
		D = !B && E.indexOf("WebKit") != -1;
		ca = !B && !D && ea.product == "Gecko"
	}
	var F = C,
		G = ca,
		fa = D,
		ga = q.navigator,
		ha = (ga && ga.platform || "").indexOf("Mac") != -1,
		ia;
	a: {
		var H = "",
			I;
		if (B && q.opera) var ja = q.opera.version,
			H = typeof ja == "function" ? ja() : ja;
		else if (G ? I = /rv\:([^\);]+)(\)|;)/ : F ? I = /MSIE\s+([^\);]+)(\)|;)/ : fa && (I = /WebKit\/(\S+)/), I) var ka = I.exec(da()),
			H = ka ? ka[1] : "";
		if (F)
		{
			var la, ma = q.document;
			la = ma ? ma.documentMode : i;
			if (la > parseFloat(H))
			{
				ia = String(la);
				break a
			}
		}
		ia = H
	}
	var na = {};

	function J(a)
	{
		return na[a] || (na[a] = aa(ia, a) >= 0)
	}
	var oa = {};

	function pa()
	{
		return oa[9] || (oa[9] = F && document.documentMode && document.documentMode >= 9)
	};
	!F || pa();
	!G && !F || F && pa() || G && J("1.9.1");
	F && J("9");
	var qa;
	!F || pa();
	var ra = F && !J("8");

	function K()
	{}
	K.prototype.p = o;
	K.prototype.d = function ()
	{
		if (!this.p) this.p = l, this.e()
	};
	K.prototype.e = function ()
	{
		this.z && sa.apply(n, this.z)
	};

	function sa(a)
	{
		for (var b = 0, d = arguments.length; b < d; ++b)
		{
			var e = arguments[b],
				g = t(e);
			g == "array" || g == "object" && typeof e.length == "number" ? sa.apply(n, e) : e && typeof e.d == "function" && e.d()
		}
	};

	function L(a, b)
	{
		this.type = a;
		this.currentTarget = this.target = b
	}
	y(L, K);
	L.prototype.e = function ()
	{
		delete this.type;
		delete this.target;
		delete this.currentTarget
	};
	L.prototype.l = o;
	L.prototype.v = l;
	L.prototype.preventDefault = function ()
	{
		this.v = o
	};

	function ta(a)
	{
		ta[" "](a);
		return a
	}
	ta[" "] = s;

	function M(a, b)
	{
		a && this.h(a, b)
	}
	y(M, L);
	p = M.prototype;
	p.target = n;
	p.relatedTarget = n;
	p.offsetX = 0;
	p.offsetY = 0;
	p.clientX = 0;
	p.clientY = 0;
	p.screenX = 0;
	p.screenY = 0;
	p.button = 0;
	p.keyCode = 0;
	p.charCode = 0;
	p.ctrlKey = o;
	p.altKey = o;
	p.shiftKey = o;
	p.metaKey = o;
	p.B = o;
	p.k = n;
	p.h = function (a, b)
	{
		var d = this.type = a.type;
		L.call(this, d);
		this.target = a.target || a.srcElement;
		this.currentTarget = b;
		var e = a.relatedTarget;
		if (e)
		{
			if (G)
			{
				var g;
				a: {
					try
					{
						ta(e.nodeName);
						g = l;
						break a
					}
					catch (c)
					{}
					g = o
				}
				g || (e = n)
			}
		}
		else if (d == "mouseover") e = a.fromElement;
		else if (d == "mouseout") e = a.toElement;
		this.relatedTarget = e;
		this.offsetX = a.offsetX !== i ? a.offsetX : a.layerX;
		this.offsetY = a.offsetY !== i ? a.offsetY : a.layerY;
		this.clientX = a.clientX !== i ? a.clientX : a.pageX;
		this.clientY = a.clientY !== i ? a.clientY : a.pageY;
		this.screenX = a.screenX || 0;
		this.screenY = a.screenY || 0;
		this.button = a.button;
		this.keyCode = a.keyCode || 0;
		this.charCode = a.charCode || (d == "keypress" ? a.keyCode : 0);
		this.ctrlKey = a.ctrlKey;
		this.altKey = a.altKey;
		this.shiftKey = a.shiftKey;
		this.metaKey = a.metaKey;
		this.B = ha ? a.metaKey : a.ctrlKey;
		this.state = a.state;
		this.k = a;
		delete this.v;
		delete this.l
	};
	p.preventDefault = function ()
	{
		M.m.preventDefault.call(this);
		var a = this.k;
		if (a.preventDefault) a.preventDefault();
		else if (a.returnValue = o, ra) try
		{
			if (a.ctrlKey || a.keyCode >= 112 && a.keyCode <= 123) a.keyCode = -1
		}
		catch (b)
		{}
	};
	p.e = function ()
	{
		M.m.e.call(this);
		this.relatedTarget = this.currentTarget = this.target = this.k = n
	};

	function ua()
	{}
	var va = 0;
	p = ua.prototype;
	p.key = 0;
	p.g = o;
	p.n = o;
	p.h = function (a, b, d, e, g, c)
	{
		if (t(a) == "function") this.r = l;
		else if (a && a.handleEvent && t(a.handleEvent) == "function") this.r = o;
		else throw Error("Invalid listener argument");
		this.i = a;
		this.u = b;
		this.src = d;
		this.type = e;
		this.capture = !! g;
		this.q = c;
		this.n = o;
		this.key = ++va;
		this.g = o
	};
	p.handleEvent = function (a)
	{
		return this.r ? this.i.call(this.q || this.src, a) : this.i.handleEvent.call(this.i, a)
	};

	function N(a, b)
	{
		this.s = b;
		this.c = [];
		if (a > this.s) throw Error("[goog.structs.SimplePool] Initial cannot be greater than max");
		for (var d = 0; d < a; d++) this.c.push(this.a ? this.a() : {})
	}
	y(N, K);
	N.prototype.a = n;
	N.prototype.o = n;

	function O(a)
	{
		return a.c.length ? a.c.pop() : a.a ? a.a() : {}
	}
	function P(a, b)
	{
		a.c.length < a.s ? a.c.push(b) : wa(a, b)
	}
	function wa(a, b)
	{
		if (a.o) a.o(b);
		else
		{
			var d = t(b);
			if (d == "object" || d == "array" || d == "function") if (t(b.d) == "function") b.d();
			else for (var e in b) delete b[e]
		}
	}
	N.prototype.e = function ()
	{
		N.m.e.call(this);
		for (var a = this.c; a.length;) wa(this, a.pop());
		delete this.c
	};
	var xa, ya = (xa = "ScriptEngine" in q && q.ScriptEngine() == "JScript") ? q.ScriptEngineMajorVersion() + "." + q.ScriptEngineMinorVersion() + "." + q.ScriptEngineBuildVersion() : "0";
	var Q, R, S, T, za, Aa, Ba, Ca, Da, Ea, Fa;
	(function ()
	{
		function a()
		{
			return {
				b: 0,
				f: 0
			}
		}
		function b()
		{
			return []
		}
		function d()
		{
			function a(b)
			{
				b = f.call(a.src, a.key, b);
				if (!b) return b
			}
			return a
		}
		function e()
		{
			return new ua
		}
		function g()
		{
			return new M
		}
		var c = xa && !(aa(ya, "5.7") >= 0),
			f;
		Aa = function (a)
		{
			f = a
		};
		if (c)
		{
			Q = function ()
			{
				return O(h)
			};
			R = function (a)
			{
				P(h, a)
			};
			S = function ()
			{
				return O(j)
			};
			T = function (a)
			{
				P(j, a)
			};
			za = function ()
			{
				return O(k)
			};
			Ba = function ()
			{
				P(k, d())
			};
			Ca = function ()
			{
				return O(v)
			};
			Da = function (a)
			{
				P(v, a)
			};
			Ea = function ()
			{
				return O(m)
			};
			Fa = function (a)
			{
				P(m, a)
			};
			var h = new N(0, 600);
			h.a = a;
			var j = new N(0, 600);
			j.a = b;
			var k = new N(0, 600);
			k.a = d;
			var v = new N(0, 600);
			v.a = e;
			var m = new N(0, 600);
			m.a = g
		}
		else Q = a, R = s, S = b, T = s, za = d, Ba = s, Ca = e, Da = s, Ea = g, Fa = s
	})();
	var U = {},
		V = {},
		W = {},
		X = {};

	function Ga(a, b, d, e, g)
	{
		if (b) if (t(b) == "array") for (var c = 0; c < b.length; c++) Ga(a, b[c], d, e, g);
		else
		{
			var e = !! e,
				f = V;
			b in f || (f[b] = Q());
			f = f[b];
			e in f || (f[e] = Q(), f.b++);
			var f = f[e],
				h = a[u] || (a[u] = ++x),
				j;
			f.f++;
			if (f[h])
			{
				j = f[h];
				for (c = 0; c < j.length; c++) if (f = j[c], f.i == d && f.q == g)
				{
					if (f.g) break;
					return
				}
			}
			else j = f[h] = S(), f.b++;
			c = za();
			c.src = a;
			f = Ca();
			f.h(d, c, a, b, e, g);
			d = f.key;
			c.key = d;
			j.push(f);
			U[d] = f;
			W[h] || (W[h] = S());
			W[h].push(f);
			a.addEventListener ? (a == q || !a.w) && a.addEventListener(b, c, e) : a.attachEvent(b in X ? X[b] : X[b] = "on" + b, c)
		}
		else throw Error("Invalid event type");
	}
	function Ha(a, b, d, e)
	{
		if (!e.j && e.t)
		{
			for (var g = 0, c = 0; g < e.length; g++) if (e[g].g)
			{
				var f = e[g].u;
				f.src = n;
				Ba(f);
				Da(e[g])
			}
			else g != c && (e[c] = e[g]), c++;
			e.length = c;
			e.t = o;
			c == 0 && (T(e), delete V[a][b][d], V[a][b].b--, V[a][b].b == 0 && (R(V[a][b]), delete V[a][b], V[a].b--), V[a].b == 0 && (R(V[a]), delete V[a]))
		}
	}

	function Ia(a, b, d, e, g)
	{
		var c = 1,
			b = b[u] || (b[u] = ++x);
		if (a[b])
		{
			a.f--;
			a = a[b];
			a.j ? a.j++ : a.j = 1;
			try
			{
				for (var f = a.length, h = 0; h < f; h++)
				{
					var j = a[h];
					j && !j.g && (c &= Ja(j, g) !== o)
				}
			}
			finally
			{
				a.j--, Ha(d, e, b, a)
			}
		}
		return Boolean(c)
	}

	function Ja(a, b)
	{
		var d = a.handleEvent(b);
		if (a.n)
		{
			var e = a.key;
			if (U[e])
			{
				var g = U[e];
				if (!g.g)
				{
					var c = g.src,
						f = g.type,
						h = g.u,
						j = g.capture;
					c.removeEventListener ? (c == q || !c.w) && c.removeEventListener(f, h, j) : c.detachEvent && c.detachEvent(f in X ? X[f] : X[f] = "on" + f, h);
					c = c[u] || (c[u] = ++x);
					h = V[f][j][c];
					if (W[c])
					{
						var k = W[c],
							v = ba(k, g);
						v >= 0 && A.splice.call(k, v, 1);
						k.length == 0 && delete W[c]
					}
					g.g = l;
					h.t = l;
					Ha(f, j, c, h);
					delete U[e]
				}
			}
		}
		return d
	}
	Aa(function (a, b)
	{
		if (!U[a]) return l;
		var d = U[a],
			e = d.type,
			g = V;
		if (!(e in g)) return l;
		var g = g[e],
			c, f;
		qa === i && (qa = F && !q.addEventListener);
		if (qa)
		{
			var h;
			if (!(h = b)) a: {
				h = "window.event".split(".");
				for (var j = q; c = h.shift();) if (j[c] != n) j = j[c];
				else
				{
					h = n;
					break a
				}
				h = j
			}
			c = h;
			h = l in g;
			j = o in g;
			if (h)
			{
				if (c.keyCode < 0 || c.returnValue != i) return l;
				a: {
					var k = o;
					if (c.keyCode == 0) try
					{
						c.keyCode = -1;
						break a
					}
					catch (v)
					{
						k = l
					}
					if (k || c.returnValue == i) c.returnValue = l
				}
			}
			k = Ea();
			k.h(c, this);
			c = l;
			try
			{
				if (h)
				{
					for (var m = S(), r = k.currentTarget; r; r = r.parentNode) m.push(r);
					f = g[l];
					f.f = f.b;
					for (var w = m.length - 1; !k.l && w >= 0 && f.f; w--) k.currentTarget = m[w], c &= Ia(f, m[w], e, l, k);
					if (j)
					{
						f = g[o];
						f.f = f.b;
						for (w = 0; !k.l && w < m.length && f.f; w++) k.currentTarget = m[w], c &= Ia(f, m[w], e, o, k)
					}
				}
				else c = Ja(d, k)
			}
			finally
			{
				if (m) m.length = 0, T(m);
				k.d();
				Fa(k)
			}
			return c
		}
		e = new M(b, this);
		try
		{
			c = Ja(d, e)
		}
		finally
		{
			e.d()
		}
		return c
	});

	function Ka()
	{
		Ga(La, "submit", this.A, o, this)
	}
	var La = document.getElementById("search");
	Ka.prototype.A = function (a)
	{
		a.preventDefault();
		a = La || document;
		a = a.querySelectorAll && a.querySelector && (!fa || document.compatMode == "CSS1Compat" || J("528")) ? a.querySelectorAll("INPUT") : a.getElementsByTagName("INPUT");
		window.location = "/search/index.html#q=" + encodeURIComponent(a[0].value)
	};
	var Y = "androidSearch".split("."),
		Z = q;
	!(Y[0] in Z) && Z.execScript && Z.execScript("var " + Y[0]);
	for (var $; Y.length && ($ = Y.shift());)!Y.length && Ka !== i ? Z[$] = Ka : Z = Z[$] ? Z[$] : Z[$] = {};
})();