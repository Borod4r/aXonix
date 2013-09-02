/*
 * Copyright 2012-2013 Ivan Gadzhega
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.ivang.axonix.main.actors.game.level.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.ivang.axonix.main.events.intents.game.DestroyBlockIntent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Gadzhega
 * @since 0.4
 */
public class BlocksParticlesHolder extends Actor {

    private Skin skin;
    private List<ParticleEffect> particleEffects;

    public BlocksParticlesHolder(Skin skin, EventBus eventBus) {
        this.skin = skin;
        this.particleEffects = new ArrayList<ParticleEffect>();
        eventBus.register(this);
    }

    @Override
    public void act(float delta) {
        for (ParticleEffect particleEffect : particleEffects) {
            if (!particleEffect.isComplete()) {
                particleEffect.update(delta);
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        for (ParticleEffect particleEffect : particleEffects) {
            if (!particleEffect.isComplete()) {
                particleEffect.draw(batch);
            }
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onBlockDestruction(DestroyBlockIntent intent) {
        ParticleEffect effect = null;
        // get some idle effect
        for (ParticleEffect particleEffect : particleEffects) {
            if (particleEffect.isComplete()) {
                effect = particleEffect;
                break;
            }
        }
        // or create new one
        if (effect == null) {
            effect = new ParticleEffect();
            effect.load(Gdx.files.internal("data/particles/block_blue.p"), skin.getAtlas());
            particleEffects.add(effect);
        }
        // and (re)run it
        Block block = intent.getBlock();
        effect.setPosition(block.getX() + 0.5f, block.getY() + 0.5f);
        effect.reset();
    }

}
