/**
 * Returns a function that, when called multiple times, cancels the previous
 * pending calls — the execution of previous calls is not actually cancelled:
 * the result is ignored and the returned promise is rejected. For this reason,
 * you must ensure you only wrap pure functions, i.e. functions without side
 * effects, since the side effects will still be executed even if the call is
 * cancelled.
 * @param {Function} fn An asynchronous **pure function**
 */
export const cancellable = function (fn) {
  let abortController = null

  return function (...args) {
    if (abortController) {
      abortController.abort()
      abortController = null
    }
    abortController = new AbortController()
    const abortSignal = abortController.signal
    return new Promise((resolve, reject) => {
      const error = new DOMException('Aborted by the user', 'AbortError')
      if (abortSignal.aborted) {
        return reject(error)
      }
      fn.call(this, ...args).then(result => resolve(result)).catch(err => reject(err))
      abortSignal.addEventListener('abort', () => {
        reject(error)
      })
    })
  }
}

export const isCancellationError = (err) => {
  return err instanceof DOMException && err.name === 'AbortError'
}
