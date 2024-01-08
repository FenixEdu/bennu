<template>
  <card-layout-wrapper>
    <template #card-content>
      <end-flow-animation :state="state">
        <template #loading-message>
          <h1 class="h3">
            {{ $t('loading.header') }}
          </h1>
          <p>
            {{ $t('loading.description', { objectId: domainObject.objectId, type: domainObject.type }) }}
          </p>
        </template>
        <template #success-message>
          <h1 class="h3">
            {{ $t('success.header') }}
          </h1>
          <p>
            {{ $t('success.description', { objectId: domainObject.objectId, type: domainObject.type }) }}
          </p>
          <router-link
            :to="{ name: 'DomainBrowserPage'}"
            class="btn btn--primary btn--full"
          >
            {{ $t('actions.back') }}
          </router-link>
        </template>
      </end-flow-animation>
    </template>
  </card-layout-wrapper>
</template>

<script>
import EndFlowAnimation, { States as EndFlowStates } from '@/components/utils/EndFlowAnimation.vue'
import CardLayoutWrapper from '@/components/CardLayoutWrapper.vue'
import { deleteDomainObject } from '@/api/bennu-admin'

export default {
  components: {
    CardLayoutWrapper,
    EndFlowAnimation
  },
  props: {
    domainObject: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      state: EndFlowStates.LOADING
    }
  },
  async mounted () {
    await this.submit()
  },
  methods: {
    async submit () {
      this.state = EndFlowStates.LOADING
      await deleteDomainObject({ objectId: this.domainObject.objectId })
      this.state = EndFlowStates.SUCCESS
    }
  },
  i18n: {
    messages: {
      pt: {
        loading: {
          header: 'A eliminar objeto',
          description: 'A eliminar objeto "{objectId}" do tipo "{type}"'
        },
        success: {
          header: 'Objeto eliminado com sucesso!',
          description: 'O objeto "{objectId}" do tipo "{type}" foi eliminado.'
        },
        actions: {
          back: 'Voltar para "Domain Browser"'
        }
      },
      en: {
        loading: {
          header: 'Deleting object',
          description: 'Deleting object "{objectId}" of type "{type}"'
        },
        success: {
          header: 'Object deleted successfully!',
          description: 'The object "{objectId}" of type "{type}" was deleted.'
        },
        actions: {
          back: 'Back to "Domain Browser"'
        }
      }
    }
  }
}
</script>
