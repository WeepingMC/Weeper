package org.bukkit.craftbukkit.legacy;

import io.papermc.paper.persistence.PersistentDataContainerView;
import java.util.Set;
import java.util.stream.Collectors;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.objectweb.asm.Opcodes;

public class PersistentDataContainerReroute {

    @FunctionalInterface
    public interface Parent {
        void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf);
    }

    public static boolean rewrite(int opcode, String owner, String name, String desc, boolean itf, Parent parent) {

        if(owner.equals("org/bukkit/persistence/PersistentDataContainer")) {
            if (name.equals("set") && desc.equals("(Lorg/bukkit/NamespacedKey;Lorg/bukkit/persistence/PersistentDataType;Ljava/lang/Object;)V")) {
                parent.visitMethodInsn(opcode, owner, name, "(Lnet/kyori/adventure/key/Key;Lorg/bukkit/persistence/PersistentDataType;Ljava/lang/Object;)V", itf);
                return true;
            }
            if(name.equals("remove") && desc.equals("(Lorg/bukkit/NamespacedKey;)V")) {
                parent.visitMethodInsn(opcode, owner, name, "(Lnet/kyori/adventure/key/Key;)V", itf);
                return true;
            }
        }

        if (owner.equals("io/papermc/paper/persistence/PersistentDataContainerView") || owner.equals("org/bukkit/persistence/PersistentDataContainer")) {
            if(name.equals("has") && desc.equals("(Lorg/bukkit/NamespacedKey;Lorg/bukkit/persistence/PersistentDataType;)Z")) {
                parent.visitMethodInsn(opcode, owner, name, "(Lnet/kyori/adventure/key/Key;Lorg/bukkit/persistence/PersistentDataType;)Z", itf);
                return true;
            }
            if(name.equals("has") && desc.equals("(Lorg/bukkit/NamespacedKey;)Z")) {
                parent.visitMethodInsn(opcode, owner, name, "(Lnet/kyori/adventure/key/Key;)Z", itf);
                return true;
            }
            if(name.equals("get") && desc.equals("(Lorg/bukkit/NamespacedKey;Lorg/bukkit/persistence/PersistentDataType;)Ljava/lang/Object;")) {
                parent.visitMethodInsn(opcode, owner, name, "(Lnet/kyori/adventure/key/Key;Lorg/bukkit/persistence/PersistentDataType;)Ljava/lang/Object;", itf);
                return true;
            }
            if(name.equals("getOrDefault") && desc.equals("(Lorg/bukkit/NamespacedKey;Lorg/bukkit/persistence/PersistentDataType;Ljava/lang/Object;)Ljava/lang/Object;")) {
                parent.visitMethodInsn(opcode, owner, name, "(Lnet/kyori/adventure/key/Key;Lorg/bukkit/persistence/PersistentDataType;Ljava/lang/Object;)Ljava/lang/Object;", itf);
                return true;
            }

            if (name.equals("getKeys")
                    && desc.equals("()Ljava/util/Set;")) {
                parent.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "org/bukkit/craftbukkit/legacy/PersistentDataContainerReroute",
                        "getKeys",
                        "(Lio/papermc/paper/persistence/PersistentDataContainerView;)Ljava/util/Set;",
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
