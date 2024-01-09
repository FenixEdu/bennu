<template>
  <card-layout-wrapper
    type="modal"
    layout="centered"
  >
    <template #icon>
      <animated-icon
        name="talking-baloon-circle-question"
        is-playing
      />
    </template>
    <template #title>
      {{ $t('header', { objectId: domainObject.objectId }) }}
    </template>
    <template #description>
      <p class="u-text-multiline">
        {{ $t('description', { objectId: domainObject.objectId, type: domainObject.type }) }}
      </p>
    </template>
    <template #card-content>
      <div class="btn-group btn-group--vertical">
        <button
          type="button"
          class="btn btn--blue btn--outline btn--full"
          @click.prevent="$emit('cancel')"
        >
          {{ $t('actions.back') }}
        </button>
        <button
          type="button"
          class="btn btn--danger btn--full"
          @click.prevent="$emit('next')"
        >
          {{ $t('actions.delete') }}
        </button>
      </div>
    </template>
  </card-layout-wrapper>
</template>

<script>
import CardLayoutWrapper from '@/components/CardLayoutWrapper.vue'
import AnimatedIcon from '@/components/utils/AnimatedIcon.vue'

export default {
  components: {
    CardLayoutWrapper,
    AnimatedIcon
  },
  props: {
    domainObject: {
      type: Object,
      required: true
    }
  },
  i18n: {
    messages: {
      pt: {
        header: 'Eliminar objeto "{objectId}"',
        description: 'Tem a certeza que pretende eliminar o objeto "{objectId}" do tipo "{type}"?\nEsta ação é irreversível e pode acarretar na deleção de outros objetos atrelados a este. Tem a certeza que deseja continuar?',
        actions: {
          back: 'Voltar',
          delete: 'Eliminar'
        }
      },
      en: {
        header: 'Delete object "{objectId}"',
        description: 'Are you sure you want to delete the object "{objectId}" of type "{type}"?\nThis action is irreversible and may result in the deletion of other objects attached to it. Are you sure you want to continue?',
        actions: {
          back: 'Back',
          delete: 'Delete'
        }
      }
    }
  }
}
</script>
