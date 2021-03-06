/**
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered.org <http://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.mod.asm.transformers;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.voxel.VoxelEvent;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class PriorityTransformer implements IClassTransformer {
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        
        if (bytes == null || !name.equals("cpw.mods.fml.common.eventhandler.EventPriority")) {
            return bytes;
        }

        try {
            ClassReader cr = new ClassReader(bytes);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, 0);

            
            
            ClassWriter cw = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
            CheckClassAdapter checker = new CheckClassAdapter(cw);
            classNode.accept(checker);
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return bytes;
        }
    }
    
    private MethodNode createGetGameMethod() {
        MethodNode methodNode = new MethodNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC, "getGame", "()Lorg/spongepowered/api/Game;", null, null);
        methodNode.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "org/spongepowered/mod/SpongeMod", "instance", "Lorg/spongepowered/mod/SpongeMod;"));
        methodNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "org/spongepowered/mod/SpongeMod", "getGame", "()Lorg/spongepowered/mod/SpongeGame;", false));
        methodNode.instructions.add(new InsnNode(Opcodes.ARETURN));
        methodNode.maxLocals = 1;
        methodNode.maxStack = 1;
        return methodNode;
    }
}
