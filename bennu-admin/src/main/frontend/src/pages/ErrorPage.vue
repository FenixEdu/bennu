<template>
  <main class="layout-centered">
    <div class="error-page">
      <div
        class="verification-page__icon"
        @click="counter++"
      >
        <animated-icon
          name="alert"
          icon-color="var(--magenta)"
          is-playing
        />
      </div>
      <div>
        <h1>{{ $t('header') }}</h1>
        <p>{{ $t('description') }}</p>

        <form
          class="card"
          @submit.prevent="sendReport"
        >
          <div class="card-row">
            <h4 class="card-row__title">
              {{ $t('form.header') }}
            </h4>
          </div>
          <div class="card-row">
            <div class="card-row__text">
              <div class="f-group error-form">
                <text-input
                  v-if="!profile || !profile.email"
                  v-model.trim="email"
                  :label="$t('form.fields.email.label')"
                  :invalid="$v.email.$error"
                  :placeholder="$t('form.fields.email.placeholder')"
                  variant="outlined"
                  name="email"
                  type="email"
                >
                  <template #error-message>
                    <template v-if="!$v.email.required">
                      {{ $t('form.fields.email.errors.required') }}
                    </template>
                    <template v-else-if="!$v.email.email">
                      {{ $t('form.fields.email.errors.invalid') }}
                    </template>
                  </template>
                </text-input>
                <text-input
                  v-model.trim="subject"
                  :label="$t('form.fields.subject.label')"
                  :placeholder="$t('form.fields.subject.placeholder')"
                  variant="outlined"
                  name="subject"
                />
                <div class="f-field">
                  <textarea
                    v-model="description"
                    class="f-field__input"
                    :placeholder="$t('form.fields.description.placeholder')"
                    name="description"
                    rows="5"
                  />
                  <label
                    for="description"
                    class="f-field__label"
                  >
                    {{ $t('form.fields.description.label') }}
                  </label>
                </div>
              </div>
            </div>
          </div>
          <div class="card-row">
            <div class="card-row__text">
              <button
                type="submit"
                class="btn btn--primary"
                :disabled="submitting"
              >
                {{ $t('submit-button') }}
              </button>
            </div>
          </div>
        </form>

        <div v-if="showError">
          <div class="error-page__error">
            <div class="error-page__error-message">
              <pre>{{ fullErrorMessage }}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import { mapState } from 'vuex'
import { sendErrorReport } from '@/api/client'
import { validationMixin } from 'vuelidate'
import required from 'vuelidate/lib/validators/required'
import email from 'vuelidate/lib/validators/email'

import AnimatedIcon from '@/components/utils/AnimatedIcon.vue'
import TextInput from '@/components/utils/fields/TextInput.vue'

export default {
  name: 'ErrorPage',
  components: {
    AnimatedIcon,
    TextInput
  },
  mixins: [validationMixin],
  props: {
    error: {
      type: Error,
      required: true
    }
  },
  i18n: {
    messages: {
      pt: {
        header: 'Erro inesperado',
        description: 'Ocorreu um erro inesperado durante a utilização da aplicação. Para que possamos ajudar, por favor preencha e submeta este formulário de erro.',
        'submit-button': 'Reportar Erro',
        form: {
          header: 'Formulário de erro',
          fields: {
            email: {
              label: 'Endereço de Email',
              placeholder: 'Email',
              errors: {
                required: 'É necessário que deixe um email para o contactarmos quando a situação se encontrar resolvida.',
                invalid: 'Este email não é válido.'
              }
            },
            subject: {
              label: 'Assunto',
              placeholder: 'Assunto'
            },
            description: {
              label: 'Descrição',
              placeholder: 'Por favor, descreva o que estava a fazer quando ocorreu o erro…'
            }
          }
        }
      },
      en: {
        header: 'Unexpected error',
        description: 'An unexpected error happened. Please fill out and submit this report form so that we may help you overcome the problem.',
        'submit-button': 'Report Error',
        form: {
          header: 'Error report form',
          fields: {
            email: {
              label: 'Email address',
              placeholder: 'Email',
              errors: {
                required: 'Leave us your email so that we can contact you when the issue is resolved.',
                invalid: 'This is not a valid email.'
              }
            },
            subject: {
              label: 'Subject',
              placeholder: 'Subject'
            },
            description: {
              label: 'Description',
              placeholder: 'Please provide a brief description of what you were doing when the error happened…'
            }
          }
        }
      }
    }
  },
  data () {
    return {
      subject: '',
      description: '',
      email: '',
      counter: 0,
      submitting: false
    }
  },
  validations: {
    email: {
      required,
      email
    }
  },
  computed: {
    ...mapState({ profile: state => state.profile }),
    requestParams () {
      return JSON.stringify(this.error.config, null, 2)
    },
    frontendStack () {
      return this.error.stack
    },
    backendResponse () {
      // Attempt to parse error response
      try {
        if (this.error.response.headers['content-type'].startsWith('text/html')) {
          const domParser = new DOMParser()
          const doc = domParser.parseFromString(this.error.response.data, 'text/html')
          // ? NOTE: "exceptionInfo" appears to only be available in production, in dev mode this will go into the fallback
          const value = doc.getElementById('exceptionInfo').value
          if (!value) {
            throw Error('Unsupported response type/format')
          }
          return value
        }
        throw Error('Unsupported response type/format')
      } catch (error) {
        // Send the entire response if parse attempt fails
        if (this.error.response.headers['content-type'].startsWith('application/json')) {
          return JSON.stringify(this.error.response.data)
        }
        return this.error.response.data
      }
    },
    fullErrorMessage () {
      // Present in all errors
      let message = `Error message: ${this.error.message}\n\nFrontend Stacktrace:\n${this.frontendStack}\n`
      // For axios errors
      if (this.requestParams) {
        message += `\nAxios request configuration:\n${this.requestParams}\n`
      }
      // For Axios errors with body
      if (this.backendResponse) {
        message += `\nBackend Response:\n${this.backendResponse}\n`
      }
      return message
    },
    showError () {
      return this.counter >= 5
    }
  },
  created () {
    if (this.profile && this.profile.email) {
      this.email = this.profile.email
    }
  },
  methods: {
    async sendReport () {
      this.$v.$touch()
      if (!this.$v.$invalid && !this.submitting) {
        this.submitting = true
        await sendErrorReport({
          email: this.email,
          subject: this.subject,
          description: this.description,
          exceptionInfo: this.fullErrorMessage
        })
        this.$router.push('/')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/scss/variables";

.layout-centered {
  text-align: center;
  margin-right: auto;
  margin-left: auto;
  margin-bottom: 1rem;
}

.error-form {
  flex-grow: 1;
  text-align: left;
}

.error-page__error-copy-svg {
  margin-right: 0.25rem;
}

.verification-page__icon {
  display: flex;
  justify-content: center;
}

.error-page__error-copy {
  top: 1rem;
  right: 2rem;
  position: absolute;
}

.error-page__error-message {
  overflow: auto;
  max-height: 40vh;
  margin: 0;
  padding: 0.5rem;
}

.error-page__error {
  text-align: left;
  position: relative;
  margin-top: 1.5rem;
  // font-family: monospace;
  border: 1px solid $gray-300;
  background: white;
}

.error-message {
  text-align: left;
}

.error-page__submit {
  margin-top: 2rem;
}

.f-field__validation {
  margin-top: 0.05rem;
  font-size: 0.855rem;
  display: flex;
  flex-direction: column;
}
</style>
