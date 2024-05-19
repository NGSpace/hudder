package io.github.ngspace.hudder.compilers;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;

import net.minecraft.item.ItemStack;

public class JavetObjConverter extends JavetProxyConverter {
	@SuppressWarnings("unchecked")
	@Override
	protected V8Value toV8Value(V8Runtime engine, Object object, int depth)
			throws JavetException {
		if (object instanceof CompileResult c) {
			try (V8ValueObject newobj = engine.createV8ValueObject()) {
				newobj.set("topleft", c.TopLeftText,
						"bottomleft", c.BottomLeftText,
						"topright", c.TopRightText,
						"bottomright", c.BottomRightText,
						"topleftscale", c.TLScale,
						"bottomleftscale", c.BLScale,
						"toprightscale", c.TRScale,
						"bottomrightscale", c.BRScale);
				return newobj;
			}
		}
		if (object instanceof ItemStack stack) {
			try (V8ValueObject newobj = engine.createV8ValueObject()) {
				newobj.set("name", stack.getName().getString(),
						"durability", stack.getMaxDamage()-stack.getDamage(),
						"maxdurability", stack.getMaxDamage(),
						"count", stack.getCount(),
						"maxcount", stack.getMaxCount());
				return newobj;
			}
		}
		return super.toV8Value(engine, object, depth);
	}
}
