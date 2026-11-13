import { quando } from '../util'

export function Tabela({ colunas, linhas, vazio }) {
  return (
    <div className="tabela">
      <table>
        <thead>
          <tr>{colunas.map((coluna) => <th key={coluna}>{coluna}</th>)}</tr>
        </thead>
        <tbody>
          {linhas.length === 0 ? (
            <tr><td colSpan={colunas.length}>{vazio}</td></tr>
          ) : linhas}
        </tbody>
      </table>
    </div>
  )
}

export function Emails({ lista }) {
  return (
    <section>
      <h2>E-mails</h2>
      <p className="prosa">Consulte o conteúdo e a situação de envio de cada aviso. O cupom também pode ser apresentado diretamente pelo extrato.</p>
      {lista.length === 0 ? <p>Nenhum e-mail registrado.</p> : lista.map((item, indice) => (
        <article className="carta" key={`${item.dataEnvio}-${indice}`}>
          <h3>{item.assunto}</h3>
          <p>{quando(item.dataEnvio)}</p>
          <p>{item.statusDescricao}</p>
          {item.enviadoEm ? <p>Enviado em {quando(item.enviadoEm)}</p> : null}
          <pre>{item.corpo}</pre>
        </article>
      ))}
    </section>
  )
}
