package org.bukkit.craftbukkit.legacy;

import io.papermc.paper.persistence.PersistentDataContainerView;
import java.lang.classfile.Opcode;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;

import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.CD_Set;
import static java.lang.constant.ConstantDescs.CD_boolean;
import static java.lang.constant.ConstantDescs.CD_void;

public class PersistentDataContainerReroute {

    private static final ClassDesc PDC = ClassDesc.of("org.bukkit.persistence", "PersistentDataContainer");
    private static final ClassDesc PDCV = ClassDesc.of("org.bukkit.persistence", "PersistentDataContainerView");

    private static final MethodTypeDesc setMethodOld = MethodTypeDesc.of(CD_void, List.of(
            ClassDesc.of("org.bukkit.NamespacedKey"),
            ClassDesc.of("org.bukkit.persistence.PersistentDataType"),
            CD_Object
    ));

    private static final MethodTypeDesc setMethodNew = MethodTypeDesc.of(CD_void, List.of(
            ClassDesc.of("net.kyori.adventure.key.Key"),
            ClassDesc.of("org.bukkit.persistence.PersistentDataType"),
            CD_Object
    ));

    private static final MethodTypeDesc removeMethodOld = MethodTypeDesc.of(CD_void, List.of(
            ClassDesc.of("org.bukkit.NamespacedKey")
    ));

    private static final MethodTypeDesc removeMethodNew = MethodTypeDesc.of(CD_void, List.of(
            ClassDesc.of("net.kyori.adventure.key.Key")
    ));

    private static final MethodTypeDesc hasMethodOld = MethodTypeDesc.of(CD_boolean, List.of(
            ClassDesc.of("org.bukkit.NamespacedKey"),
            ClassDesc.of("org.bukkit.persistence.PersistentDataType")
    ));

    private static final MethodTypeDesc hasMethodNew = MethodTypeDesc.of(CD_boolean, List.of(
            ClassDesc.of("net.kyori.adventure.key.Key"),
            ClassDesc.of("org.bukkit.persistence.PersistentDataType")
    ));

    private static final MethodTypeDesc hasMethodOldNoType = MethodTypeDesc.of(CD_boolean, List.of(
            ClassDesc.of("org.bukkit.NamespacedKey")
    ));

    private static final MethodTypeDesc hasMethodNewNoType = MethodTypeDesc.of(CD_boolean, List.of(
            ClassDesc.of("net.kyori.adventure.key.Key")
    ));

    private static final MethodTypeDesc getMethodOld = MethodTypeDesc.of(CD_Object, List.of(
            ClassDesc.of("org.bukkit.NamespacedKey"),
            ClassDesc.of("org.bukkit.persistence.PersistentDataType")
    ));

    private static final MethodTypeDesc getMethodNew = MethodTypeDesc.of(CD_Object, List.of(
            ClassDesc.of("net.kyori.adventure.key.Key"),
            ClassDesc.of("org.bukkit.persistence.PersistentDataType")
    ));

    private static final MethodTypeDesc getOrDefaultMethodOld = MethodTypeDesc.of(CD_Object, List.of(
            ClassDesc.of("org.bukkit.NamespacedKey"),
            ClassDesc.of("org.bukkit.persistence.PersistentDataType"),
            CD_Object
    ));

    private static final MethodTypeDesc getOrDefaultMethodNew = MethodTypeDesc.of(CD_Object, List.of(
            ClassDesc.of("net.kyori.adventure.key.Key"),
            ClassDesc.of("org.bukkit.persistence.PersistentDataType"),
            CD_Object
    ));

    private static final MethodTypeDesc getKeysMethodOld = MethodTypeDesc.of(CD_Set);

    private static final ClassDesc legacy = ClassDesc.ofInternalName("org/bukkit/craftbukkit/legacy/PersistentDataContainerReroute");

    private static final MethodTypeDesc getKeysMethodNew = MethodTypeDesc.of(CD_Set, ClassDesc.of("io.papermc.paper.persistence.PersistentDataContainerView"));


    @FunctionalInterface
    public interface Parent {
        void visitMethodInsn(Opcode opcode, ClassDesc owner, String name, MethodTypeDesc desc, boolean itf);
    }

    public static boolean rewrite(Opcode opcode, ClassDesc owner, String name, MethodTypeDesc desc, boolean itf, Parent parent) {
        if (owner.equals(PDC)) {
            if (name.equals("set") && desc.equals(setMethodOld)) {
                parent.visitMethodInsn(opcode, owner, name, setMethodNew, itf);
                return true;
            }
            if (name.equals("remove") && desc.equals(removeMethodOld)) {
                parent.visitMethodInsn(opcode, owner, name, removeMethodNew, itf);
                return true;
            }
        }

        if (owner.equals(PDCV) || owner.equals(PDC)) {
            if (name.equals("has") && desc.equals(hasMethodOld)) {
                parent.visitMethodInsn(opcode, owner, name, hasMethodNew, itf);
                return true;
            }
            if (name.equals("has") && desc.equals(hasMethodOldNoType)) {
                parent.visitMethodInsn(opcode, owner, name, hasMethodNewNoType, itf);
                return true;
            }
            if (name.equals("get") && desc.equals(getMethodOld)) {
                parent.visitMethodInsn(opcode, owner, name, getMethodNew, itf);
                return true;
            }
            if (name.equals("getOrDefault") && desc.equals(getOrDefaultMethodOld)) {
                parent.visitMethodInsn(opcode, owner, name, getOrDefaultMethodNew, itf);
                return true;
            }

            if (name.equals("getKeys")
                    && desc.equals(getKeysMethodOld)) {
                parent.visitMethodInsn(
                        Opcode.INVOKESTATIC,
                        legacy,
                        "getKeys",
                        getKeysMethodNew,
                        false
                );
                return true;
            }
        }
        return false;
    }

    public static Set<NamespacedKey> getKeys(final PersistentDataContainerView view) {
        return view.keys().stream()
                .map(PersistentDataContainerReroute::toNamespacedKey)
                .collect(Collectors.toSet());
    }

    private static NamespacedKey toNamespacedKey(final Key key) {
        return new NamespacedKey(key.namespace(), key.value());
    }
}
