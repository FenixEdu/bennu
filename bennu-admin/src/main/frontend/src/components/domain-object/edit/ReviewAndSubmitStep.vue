<template>
  <div>
    <header class="section-header page__header">
      <div class="section-header__text">
        <h1 class="h1 section-header__title">
          {{ $t('header', { object: domainObject.objectId }) }}
        </h1>
        <p>{{ $t('description') }}</p>
      </div>
    </header>
    <view-dynamic-form
      class="card-wrapper"
      :dynamic-form="form"
    >
      <template #form-section-meta="{page,pageIndex,sectionIndex,section}">
        <slot
          name="form-section-meta"
          :page="page"
          :page-index="pageIndex"
          :section="section"
          :section-index="sectionIndex"
        />
      </template>
    </view-dynamic-form>
    <form
      class="step__confirm-form"
      @submit.prevent="submit()"
    >
      <fieldset
        :disabled="submitState !== SubmitState.IDLE"
        class="btn-group"
      >
        <button
          class="btn btn--light"
          type="button"
          @click="back"
        >
          {{ $t('actions.cancel') }}
        </button>
        <submit-button
          :disabled="submitState !== SubmitState.IDLE"
          :submitted="submitState === SubmitState.SUCCESS"
          :submitting="submitState === SubmitState.SUBMITTING"
          type="submit"
        >
          <template #submit-text>
            {{ $t('actions.submit.submit') }}
          </template>
          <template #submitting-text>
            {{ $t('actions.submit.submitting') }}
          </template>
          <template #submitted-text>
            {{ $t('actions.submit.submitted') }}
          </template>
        </submit-button>
      </fieldset>
    </form>
  </div>
</template>

<script>
import ViewDynamicForm from '@/components/dynamic-form/ViewDynamicForm.vue'
import TranslateApiString from '@/mixins/TranslateApiString'
import { DynamicForm } from '@/components/dynamic-form/utils/form'
import { sleep } from '@/utils/misc'
import { editDomainObject } from '@/api/bennu-admin'
import SubmitButton from '@/components/utils/SubmitButton.vue'

const SubmitState = Object.freeze({
  IDLE: 'idle',
  SUBMITTING: 'submitting',
  SUCCESS: 'success',
  ERROR: 'error'
})

export default {
  components: {
    ViewDynamicForm,
    SubmitButton
  },
  mixins: [TranslateApiString],
  props: {
    domainObject: {
      type: Object,
      required: true
    },
    domainObjectForm: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      form: new DynamicForm(this.domainObjectForm.form, this.domainObjectForm.data),
      submitState: SubmitState.IDLE,
      SubmitState
    }
  },
  methods: {
    back () {
      this.$emit('previous')
    },
    async submit () {
      this.submitState = SubmitState.SUBMITTING
      try {
        const data = await this.form.getSubmitData()

        await Promise.all([
          sleep(2000), // lets loading animation run for at least two seconds
          editDomainObject({ objectId: this.domainObject.objectId, data })
        ])

        this.submitState = SubmitState.SUCCESS
        await this.$router.replace({ name: 'DomainObjectPage', params: { objectId: this.domainObject.objectId } })
      } catch (error) {
        // todo: handle errors
        this.submitState = SubmitState.ERROR
      }
    }
  },
  i18n: {
    messages: {
      en: {
        header: 'Edit object {object}',
        description: 'Review and submit your changes',
        review: 'Review and submit',
        actions: {
          cancel: 'Cancel',
          submit: {
            submit: 'Submit',
            submitting: 'Submitting...',
            submitted: 'Submitted'
          }
        }
      },
      pt: {
        header: 'Resumo do formulário de edição do objeto "{object}"',
        description: 'Verifique se os dados do formulário estão corretos antes de submeter o seu pedido de alteração.',
        actions: {
          cancel: 'Cancelar',
          submit: {
            submit: 'Submeter',
            submitting: 'A submeter...',
            submitted: 'Submetido'
          }
        }
      }
    }
  }
}

</script>

<style lang="scss" scoped>
.step__confirm-form {
  margin-top: 1.5rem;
}
</style>
