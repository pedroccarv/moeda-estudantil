export async function api(path, options = {}) {
  const opts = { credentials: 'include', method: options.method || 'GET' }
  if (options.json !== undefined) {
    opts.headers = { 'Content-Type': 'application/json' }
    opts.body = JSON.stringify(options.json)
  } else if (options.body) {
    opts.body = options.body
  }
  const response = await fetch(path, opts)
  const text = await response.text()
  let data = null
  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      data = null
    }
  }
  if (!response.ok) {
    const error = new Error((data && data.mensagem) || 'Não foi possível concluir.')
    error.status = response.status
    error.aviso = data && data.aviso
    throw error
  }
  return data
}
